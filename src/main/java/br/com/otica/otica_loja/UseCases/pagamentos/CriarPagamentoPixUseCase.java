package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.dto.PixPagamentoResponse;
import br.com.otica.otica_loja.enums.StatusPedido;

// 1. IMPORTS CORRETOS DO JACKSON DO SPRING BOOT
import br.com.otica.otica_loja.enums.TipoEmail;
import br.com.otica.otica_loja.service.email.EmailService;
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

    @Autowired
    private EmailService emailService;

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

            String blocoPixHtml = "<div style='background-color: #f8f9fa; border: 1px dashed #004d99; padding: 20px; text-align: center; margin: 20px 0; border-radius: 8px;'>" +
                    "<h3 style='margin-top: 0; color: #004d99; font-family: Arial, sans-serif;'>Pague via PIX para liberar seu pedido!</h3>" +
                    (qrCodeBase64 != null && !qrCodeBase64.isBlank() ? "<img src='data:image/png;base64," + qrCodeBase64 + "' width='180' height='180' style='margin: 15px 0;'><br>" : "") +
                    "<p style='font-size: 13px; color: #555; margin-bottom: 8px; font-family: Arial, sans-serif;'><b>Ou copie e cole o código abaixo no seu banco:</b></p>" +
                    "<input type='text' value='" + chavePixCopiaECola + "' readonly style='width: 95%; padding: 12px; border: 1px solid #ced4da; font-size: 12px; text-align: center; background: #ffffff; border-radius: 4px; color: #495057;'>" +
                    "</div>";

            emailService.enviarEmailPedido(pedido, TipoEmail.PEDIDO_REALIZADO, blocoPixHtml);
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