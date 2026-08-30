package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.UseCases.pagamentos.ConfirmarPagamentoUseCase;
import br.com.otica.otica_loja.dto.ConfirmarPagamentoRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


import java.util.UUID;

@RestController
@RequestMapping("/api/pagamentos")
public class WebhookController {

    @Autowired
    private ConfirmarPagamentoUseCase confirmarPagamentoUseCase;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostMapping("/webhook")
    public ResponseEntity<Void> receberNotificacao(
            @RequestParam(name = "topic", required = false) String topic,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "data.id", required = false) String dataId) {

        // O MP pode enviar "topic" ou "type". Validar ambos.
        String eventType = topic != null ? topic : type;
        String resourceId = dataId != null ? dataId : id;

        if (("payment".equals(eventType) || "order".equals(eventType)) && resourceId != null) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                HttpEntity<Void> entity = new HttpEntity<>(headers);

                // Como a aplicação agora usa a API de Orders, o recurso principal é uma Order
                String urlConsulta = "order".equals(eventType)
                        ? "https://api.mercadopago.com/v1/orders/" + resourceId
                        : "https://api.mercadopago.com/v1/payments/" + resourceId; // Fallback se vier de pagamento legado

                ResponseEntity<String> response = restTemplate.exchange(
                        urlConsulta,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());

                boolean isAprovado = false;

                String externalReference = root.path("external_reference").asString();
                String paymentIdForDb = "";
                String paymentMethodId = "";

                if ("order".equals(eventType)) {
                    String orderStatus = root.path("status").asString();
                    String orderStatusDetail = root.path("status_detail").asString();

                    // Na API de Orders, quando tudo é pago com sucesso, o status é "processed" e o detail "accredited"
                    isAprovado = "processed".equals(orderStatus) && "accredited".equals(orderStatusDetail);

                    // Extraindo o ID do pagamento e a forma de pagamento de dentro da Order
                    JsonNode paymentsArray = root.path("transactions").path("payments");
                    if (paymentsArray.isArray() && !paymentsArray.isEmpty()) {
                        paymentIdForDb = paymentsArray.get(0).path("id").asString();
                        paymentMethodId = paymentsArray.get(0).path("payment_method").path("id").asString();
                    }

                } else {
                    // Retorno legado (Payment v1)
                    isAprovado = "approved".equals(root.path("status").asString());
                    paymentIdForDb = resourceId;
                    paymentMethodId = root.path("payment_method_id").asString();
                }

                // Se estiver aprovado, atualiza o nosso banco usando o ConfirmarPagamentoUseCase
                if (isAprovado && externalReference != null && !externalReference.isEmpty()) {

                    UUID pedidoId = UUID.fromString(externalReference);

                    ConfirmarPagamentoRequest request = new ConfirmarPagamentoRequest(
                            pedidoId,
                            null, // null indica que veio do webhook automático
                            paymentMethodId,
                            paymentIdForDb
                    );

                    confirmarPagamentoUseCase.confirmarPagamento(request);
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar Webhook do Mercado Pago (Orders API): " + e.getMessage());
            }
        }

        // SEMPRE responda 200 OK imediatamente para o MP registrar o recebimento
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}