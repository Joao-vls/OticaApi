package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.dto.PixPagamentoResponse;
import br.com.otica.otica_loja.enums.StatusPedido;

// 1. IMPORTS CORRETOS DO JACKSON DO SPRING BOOT
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Locale;

@Service
public class CriarPagamentoPixUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public PixPagamentoResponse criarPagamento(UUID pedidoId, UUID usuarioId, String emailCliente) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (usuarioId != null && !pedido.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("O usuário informado não pertence a este pedido.");
        }

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido não está aguardando pagamento.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            // 2. Formatar o valor estritamente para 2 casas decimais (ex: 50.00)
            String totalAmountStr = String.format(Locale.US, "%.2f", pedido.getTotal());

            Map<String, Object> body = new HashMap<>();
            body.put("type", "online");
            body.put("external_reference", pedido.getId().toString());
            body.put("processing_mode", "automatic");
            body.put("total_amount", totalAmountStr);
            body.put("description", "Pedido Ótica - " + pedido.getId());

            Map<String, Object> payer = new HashMap<>();
            payer.put("email", emailCliente);
            body.put("payer", payer);

            Map<String, Object> paymentMethod = new HashMap<>();
            paymentMethod.put("id", "pix");
            paymentMethod.put("type", "bank_transfer");

            Map<String, Object> paymentItem = new HashMap<>();
            paymentItem.put("amount", totalAmountStr);
            paymentItem.put("payment_method", paymentMethod);
            // "expiration_time" removido pois o MP já assume 24h por padrão e pode conflitar.

            Map<String, Object> transactions = new HashMap<>();
            transactions.put("payments", List.of(paymentItem));

            body.put("transactions", transactions);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/orders",
                    entity,
                    String.class
            );

            JsonNode root = mapper.readTree(response.getBody());

            // 3. USO CORRETO DO asText() COM O JACKSON DO SPRING BOOT
            String orderIdMercadoPago = root.path("id").asText(null);

            JsonNode firstPayment = root.path("transactions").path("payments").get(0);
            JsonNode methodResponse = firstPayment.path("payment_method");

            // Extraindo as chaves corretamente
            String chavePixCopiaECola = methodResponse.path("qr_code").asText("");
            String qrCodeBase64 = methodResponse.path("qr_code_base64").asText("");

            pedido.setOrderIdMercadoPago(orderIdMercadoPago);
            pedido.setStatus(StatusPedido.PROCESSANDO);
            pedido.setAtualizadoEm(OffsetDateTime.now());
            pedidoRepository.save(pedido);

            return new PixPagamentoResponse(
                    pedido.getId(),
                    pedido.getTotal(),
                    chavePixCopiaECola,
                    qrCodeBase64,
                    "Pix gerado com sucesso"
            );

        } catch (Exception e) {
            e.printStackTrace(); // Útil para ver o erro no log do Spring
            throw new RuntimeException("Erro ao gerar Pix no Mercado Pago: " + e.getMessage(), e);
        }
    }
}