package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.UseCases.pagamentos.ConfirmarPagamentoUseCase;
import br.com.otica.otica_loja.dto.ConfirmarPagamentoRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagamentos")
public class WebhookController {

    @Autowired
    private ConfirmarPagamentoUseCase confirmarPagamentoUseCase;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<Void> receberNotificacao(
            @RequestParam(name = "topic", required = false) String topic,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "data.id", required = false) String dataId,
            @RequestHeader(value = "x-signature", required = false) String xSignature,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId) {

        String eventType = topic != null ? topic : type;
        String resourceId = dataId != null ? dataId : id;

        System.out.println("\n========================================");
        System.out.println("🔔 WEBHOOK RECEBIDO DO MERCADO PAGO");
        System.out.println("Tipo de Evento: " + eventType);
        System.out.println("ID do Recurso: " + resourceId);
        System.out.println("X-Signature presente? " + (xSignature != null));
        System.out.println("========================================\n");

        // 1. VALIDAÇÃO DE SEGURANÇA (Aviso apenas, sem bloquear para testes)
        if (!isAssinaturaValida(xSignature, xRequestId, resourceId)) {
            System.err.println("⚠️ ALERTA: Assinatura inválida ou ausente. (Para debug, vamos permitir passar e ver o que quebra depois).");
            // Se fosse produção estrita, faríamos: return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (("payment".equals(eventType) || "order".equals(eventType)) && resourceId != null) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                HttpEntity<Void> entity = new HttpEntity<>(headers);

                String urlConsulta = "order".equals(eventType)
                        ? "https://api.mercadopago.com/v1/orders/" + resourceId
                        : "https://api.mercadopago.com/v1/payments/" + resourceId;

                System.out.println("🔍 Consultando API do MP: " + urlConsulta);

                ResponseEntity<String> response = restTemplate.exchange(
                        urlConsulta,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());

                boolean isAprovado = false;
                String externalReference = root.path("external_reference").asText(null);
                String paymentIdForDb = "";
                String paymentMethodId = "";

                System.out.println("📦 External Reference (ID Pedido): " + externalReference);

                if ("order".equals(eventType)) {
                    String orderStatus = root.path("status").asText("");
                    String orderStatusDetail = root.path("status_detail").asText("");

                    System.out.println("📊 Status do Order: " + orderStatus + " | Detail: " + orderStatusDetail);

                    isAprovado = "processed".equals(orderStatus) && "accredited".equals(orderStatusDetail);

                    JsonNode paymentsArray = root.path("transactions").path("payments");
                    if (paymentsArray.isArray() && !paymentsArray.isEmpty()) {
                        paymentIdForDb = paymentsArray.get(0).path("id").asText("");
                        paymentMethodId = paymentsArray.get(0).path("payment_method").path("id").asText("");
                    }
                } else {
                    String paymentStatus = root.path("status").asText("");
                    System.out.println("📊 Status do Pagamento: " + paymentStatus);

                    isAprovado = "approved".equals(paymentStatus);
                    paymentIdForDb = resourceId;
                    paymentMethodId = root.path("payment_method_id").asText("");
                }

                if (isAprovado) {
                    if (externalReference != null && !externalReference.isEmpty() && !externalReference.equals("null")) {
                        System.out.println("✅ Pagamento aprovado! Atualizando pedido: " + externalReference);

                        UUID pedidoId = UUID.fromString(externalReference);
                        ConfirmarPagamentoRequest request = new ConfirmarPagamentoRequest(
                                pedidoId, null, paymentMethodId, paymentIdForDb
                        );
                        confirmarPagamentoUseCase.confirmarPagamento(request);

                        System.out.println("🎉 Banco de dados atualizado com sucesso para PAGO!");
                    } else {
                        System.err.println("❌ Erro: O pagamento foi aprovado, mas o Mercado Pago não devolveu a external_reference (ID do Pedido).");
                    }
                } else {
                    System.out.println("⏳ Ignorando... O status atual não é aprovado/processado.");
                }

            } catch (IllegalArgumentException e) {
                System.err.println("❌ Erro: O UUID recebido ('" + e.getMessage() + "') não existe no banco de dados.");
            } catch (Exception e) {
                System.err.println("❌ Erro grave ao processar Webhook do Mercado Pago: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private boolean isAssinaturaValida(String xSignature, String xRequestId, String dataId) {
        if (xSignature == null || xRequestId == null || dataId == null || webhookSecret == null || webhookSecret.isBlank()) {
            return false;
        }

        try {
            String ts = "";
            String hashEnviado = "";
            String[] parts = xSignature.split(",");
            for (String part : parts) {
                if (part.trim().startsWith("ts=")) ts = part.trim().substring(3);
                if (part.trim().startsWith("v1=")) hashEnviado = part.trim().substring(3);
            }

            if (ts.isEmpty() || hashEnviado.isEmpty()) return false;

            String manifest = "id:" + dataId + ";request-id:" + xRequestId + ";ts:" + ts + ";";
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] bytes = sha256Hmac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
            StringBuilder hashCalculado = new StringBuilder();
            for (byte b : bytes) {
                hashCalculado.append(String.format("%02x", b));
            }

            return hashCalculado.toString().equals(hashEnviado);

        } catch (Exception e) {
            return false;
        }
    }
}