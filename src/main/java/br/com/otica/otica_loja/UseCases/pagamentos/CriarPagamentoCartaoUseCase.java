package br.com.otica.otica_loja.UseCases.pagamentos;

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

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public CartaoPagamentoResponse criarPagamento(CartaoPagamentoRequest request) {
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
            // Chave de idempotência para evitar cobrança duplicada caso a rede oscile
            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

            // ==========================================
            // CRIAÇÃO DA ORDER (Modo Automático - Cartão)
            // ==========================================
            Map<String, Object> body = new HashMap<>();
            body.put("type", "online");
            body.put("external_reference", pedido.getId().toString());
            body.put("processing_mode", "automatic"); // O cartão cobra e processa na mesma chamada
            body.put("total_amount", String.valueOf(pedido.getTotal()));
            body.put("description", "Pedido Ótica - " + pedido.getId());

            Map<String, Object> payer = new HashMap<>();
            payer.put("email", request.emailCliente());
            payer.put("first_name", request.nomeCliente());
            payer.put("last_name", request.sobrenomeCliente());
            body.put("payer", payer);

            // Mapeia os dados do Brick (token, método e parcelas)
            Map<String, Object> paymentMethod = new HashMap<>();
            paymentMethod.put("id", request.paymentMethodId()); // ex: "master", "visa"
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
            String challengeUrl = null;

            // Tratamento opcional para 3DS Challenge (exigência de autenticação do banco emissor)
            if (paymentData.has("three_ds_info") && paymentData.path("three_ds_info").has("challenge_url")) {
                challengeUrl = paymentData.path("three_ds_info").path("challenge_url").asText();
            }

            // Se aprovado ou em processamento, atualiza no banco
            if ("processed".equals(statusMP) || "approved".equals(statusMP) || "in_process".equals(statusMP)) {
                pedido.setStatus(StatusPedido.PROCESSANDO);
                pedido.setAtualizadoEm(OffsetDateTime.now());
                pedidoRepository.save(pedido);
            }

            return new CartaoPagamentoResponse(
                    pedido.getId(),
                    statusMP,
                    challengeUrl,
                    "Pagamento via cartão enviado com sucesso"
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar pagamento de cartão no Mercado Pago: " + e.getMessage(), e);
        }
    }
}