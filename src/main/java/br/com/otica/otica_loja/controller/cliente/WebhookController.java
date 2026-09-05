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
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
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
        // O resourceId real para validação do manifesto costuma ser o dataId em minúsculo
        String resourceId = dataId != null ? dataId.toLowerCase() : (id != null ? id.toLowerCase() : null);

        // 🔒 SEGURANÇA ESTRITA: Validação baseada na documentação oficial do Mercado Pago
        if (!isAssinaturaValida(xSignature, xRequestId, resourceId)) {
            System.err.println("Tentativa de Webhook sem assinatura válida bloqueada por segurança.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String targetId = dataId != null ? dataId : id;

        if (("payment".equals(eventType) || "order".equals(eventType)) && targetId != null) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                HttpEntity<Void> entity = new HttpEntity<>(headers);

                String urlConsulta = "order".equals(eventType)
                        ? "https://api.mercadopago.com/v1/orders/" + targetId
                        : "https://api.mercadopago.com/v1/payments/" + targetId;

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

                if ("order".equals(eventType)) {
                    String orderStatus = root.path("status").asText("");
                    String orderStatusDetail = root.path("status_detail").asText("");

                    isAprovado = "processed".equals(orderStatus) && "accredited".equals(orderStatusDetail);

                    JsonNode paymentsArray = root.path("transactions").path("payments");
                    if (paymentsArray.isArray() && !paymentsArray.isEmpty()) {
                        paymentIdForDb = paymentsArray.get(0).path("id").asText("");
                        paymentMethodId = paymentsArray.get(0).path("payment_method").path("id").asText("");
                    }
                } else {
                    String paymentStatus = root.path("status").asText("");
                    isAprovado = "approved".equals(paymentStatus);
                    paymentIdForDb = targetId;
                    paymentMethodId = root.path("payment_method_id").asText("");
                }

                if (isAprovado && externalReference != null && !externalReference.isEmpty() && !externalReference.equals("null")) {
                    UUID pedidoId = UUID.fromString(externalReference);

                    ConfirmarPagamentoRequest request = new ConfirmarPagamentoRequest(
                            pedidoId, null, paymentMethodId, paymentIdForDb
                    );

                    confirmarPagamentoUseCase.confirmarPagamento(request);
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Erro: O UUID recebido ('" + e.getMessage() + "') não existe no banco de dados.");
            } catch (Exception e) {
                System.err.println("Erro grave ao processar Webhook do Mercado Pago: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Validação oficial de assinatura baseada estritamente na documentação do SDK Java do Mercado Pago.
     */
    private boolean isAssinaturaValida(String xSignature, String xRequestId, String dataId) {
        if (webhookSecret == null || webhookSecret.isBlank() || xSignature == null || xRequestId == null) {
            return false;
        }

        try {
            String ts = null;
            String hashEnviado = null;

            for (String part : xSignature.split(",")) {
                String[] kv = part.split("=", 2);
                if (kv.length != 2) continue;
                String key = kv[0].trim();
                String val = kv[1].trim();
                if ("ts".equals(key)) ts = val;
                if ("v1".equals(key)) hashEnviado = val;
            }

            if (ts == null || hashEnviado == null) {
                return false;
            }

            // Constrói o manifesto oficial omitindo valores vazios conforme a especificação
            List<String> parts = new ArrayList<>();
            if (dataId != null && !dataId.isEmpty()) {
                parts.add("id:" + dataId);
            }
            if (xRequestId != null && !xRequestId.isEmpty()) {
                parts.add("request-id:" + xRequestId);
            }
            parts.add("ts:" + ts);
            String manifest = String.join(";", parts) + ";";

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] bytes = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            String computed = sb.toString();

            // Comparação segura de tempo constante
            return MessageDigest.isEqual(
                    computed.getBytes(StandardCharsets.UTF_8),
                    hashEnviado.getBytes(StandardCharsets.UTF_8)
            );

        } catch (Exception e) {
            System.err.println("Erro ao validar assinatura criptográfica do webhook: " + e.getMessage());
            return false;
        }
    }
}