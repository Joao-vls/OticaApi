package br.com.otica.otica_loja.UseCases.pagamentos;

// ... mantenha os imports que já existiam e adicione o CarrinhoRepository:
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.dto.CartaoPagamentoRequest;
import br.com.otica.otica_loja.dto.CartaoPagamentoResponse;
import br.com.otica.otica_loja.enums.StatusPedido;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CriarPagamentoCartaoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CarrinhoRepository carrinhoRepository; // 👈 Novo

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public CartaoPagamentoResponse criarPagamento(CartaoPagamentoRequest request, UUID usuarioId) {
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido não está aguardando pagamento.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

            Map<String, Object> body = new HashMap<>();
            body.put("type", "online");
            body.put("external_reference", pedido.getId().toString());
            body.put("processing_mode", "automatic");
            body.put("total_amount", String.valueOf(pedido.getTotal()));
            body.put("description", "Pedido Ótica - " + pedido.getId());

            Map<String, Object> payer = new HashMap<>();
            payer.put("email", request.emailCliente());
            payer.put("first_name", request.nomeCliente());
            payer.put("last_name", request.sobrenomeCliente());
            body.put("payer", payer);

            Map<String, Object> paymentMethod = new HashMap<>();
            paymentMethod.put("id", request.paymentMethodId());
            paymentMethod.put("type", "credit_card");
            paymentMethod.put("token", request.tokenGeradoPeloFrontEnd());
            paymentMethod.put("installments", request.parcelas());

            Map<String, Object> paymentItem = new HashMap<>();
            paymentItem.put("amount", String.valueOf(pedido.getTotal()));
            paymentItem.put("payment_method", paymentMethod);

            Map<String, Object> transactions = new HashMap<>();
            transactions.put("payments", List.of(paymentItem));
            body.put("transactions", transactions);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/orders",
                    requestEntity,
                    String.class
            );

            JsonNode root = mapper.readTree(response.getBody());
            JsonNode paymentData = root.path("transactions").path("payments").get(0);

            String statusMP = paymentData.path("status").asText("");
            String paymentId = paymentData.path("id").asText("");
            String challengeUrl = null;

            if (paymentData.has("three_ds_info") && paymentData.path("three_ds_info").has("challenge_url")) {
                challengeUrl = paymentData.path("three_ds_info").path("challenge_url").asText();
            }

            // =========================================================================
            // 3. VALIDAÇÃO DE SUCESSO: ALTERA PEDIDO E LIMPA CARRINHO SÓ SE APROVAR
            // =========================================================================
            if ("processed".equals(statusMP) || "approved".equals(statusMP) || "in_process".equals(statusMP)) {
                pedido.setStatus(StatusPedido.PROCESSANDO);
                pedido.setAtualizadoEm(OffsetDateTime.now());
                pedido.setOrderIdMercadoPago(paymentId);
                pedido.setObservacoes("Pagamento via Cartão aprovado/processado. ID Transação: " + paymentId);
                pedidoRepository.save(pedido);

                // 👇 LIMPA O CARRINHO AQUI (PAGAMENTO APROVADO!) 👇
                carrinhoRepository.findByUsuarioId(pedido.getUsuarioId()).ifPresent(carrinho -> {
                    carrinho.getItens().clear();
                    carrinhoRepository.save(carrinho);
                });
            } else {
                throw new RuntimeException("Pagamento recusado pelo banco emissor (Status: " + statusMP + ").");
            }

            return new CartaoPagamentoResponse(pedido.getId(), statusMP, challengeUrl, "Pagamento processado com sucesso");

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage().contains("recusado") ? e.getMessage() : "Erro ao processar pagamento de cartão: " + e.getMessage());
        }
    }
}