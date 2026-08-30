package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.dto.CartaoPagamentoRequest;
import br.com.otica.otica_loja.dto.CartaoPagamentoResponse;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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

    public CartaoPagamentoResponse criarPagamento(UUID pedidoId, UUID usuarioId, CartaoPagamentoRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        // 🎯 Uso do usuarioId: Validação de segurança para garantir o dono do pedido
        if (usuarioId != null && !pedido.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("O usuário informado não pertence a este pedido.");
        }

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("O pedido já não está mais aguardando pagamento.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            // 1. Montando o Body
            Map<String, Object> body = new HashMap<>();
            body.put("type", "online");
            body.put("external_reference", pedido.getId().toString());
            body.put("processing_mode", "automatic");
            body.put("capture_mode", "automatic_async");
            body.put("total_amount", String.valueOf(pedido.getTotal()));
            body.put("description", "Pedido Ótica - " + pedido.getId());

            // 2. Configuração do 3DS 2.0
            Map<String, Object> transactionSecurity = new HashMap<>();
            transactionSecurity.put("validation", "on_fraud_risk");
            transactionSecurity.put("liability_shift", "required");

            Map<String, Object> onlineConfig = new HashMap<>();
            onlineConfig.put("transaction_security", transactionSecurity);

            Map<String, Object> config = new HashMap<>();
            config.put("online", onlineConfig);

            body.put("config", config);

            // 3. Dados do Pagador (Payer)
            Map<String, Object> payer = new HashMap<>();
            String emailParaAPI = request.emailCliente();

            if (accessToken.startsWith("TEST-") || accessToken.contains("TEST")) {
                String alias = emailParaAPI.split("@")[0];
                emailParaAPI = alias + "@testuser.com";
            }
            payer.put("email", emailParaAPI);

            // --- TRATAMENTO DOS NOMES ---
            String nome = (request.nomeCliente() != null && !request.nomeCliente().isBlank())
                    ? request.nomeCliente().trim()
                    : "Cliente";

            String sobrenome = (request.sobrenomeCliente() != null && !request.sobrenomeCliente().isBlank())
                    ? request.sobrenomeCliente().trim()
                    : "";

            // Se o sobrenome estiver vazio, tenta extrair a segunda palavra do nome
            if (sobrenome.isEmpty()) {
                String[] partesNome = nome.split("\\s+", 2);
                if (partesNome.length > 1) {
                    nome = partesNome[0];
                    sobrenome = partesNome[1];
                } else {
                    // Fallback para caso o usuário informe apenas um primeiro nome
                    sobrenome = "Sobrenome";
                }
            }

            payer.put("first_name", nome);
            payer.put("last_name", sobrenome);
            body.put("payer", payer);
            // 4. Método de Pagamento
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

            // 5. Configurando Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // 6. Chamada para a API de Orders
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/orders",
                    entity,
                    String.class
            );

            JsonNode root = mapper.readTree(response.getBody());

            // 🎯 Extração do ORDER ID usando asString() do Jackson 3
            String orderIdMercadoPago = root.path("id").asString();

            String orderStatus = root.path("status").asString();
            String orderStatusDetail = root.path("status_detail").asString();

            if ("processed".equals(orderStatus) || "action_required".equals(orderStatus)) {
                pedido.setStatus(StatusPedido.PROCESSANDO);
            } else if ("failed".equals(orderStatus)) {
                pedido.setStatus(StatusPedido.CANCELADO);
            }

            pedido.setOrderIdMercadoPago(orderIdMercadoPago);
            pedido.setAtualizadoEm(OffsetDateTime.now());
            pedidoRepository.save(pedido);

            // Extraindo dados do pagamento e URL de Challenge (3DS) se houver
            String transactionId = "";
            String challengeUrl = null;

            JsonNode paymentsNode = root.path("transactions").path("payments");
            if (paymentsNode.isArray() && !paymentsNode.isEmpty()) {
                JsonNode firstPayment = paymentsNode.get(0);
                transactionId = firstPayment.path("id").asString();

                JsonNode securityUrlNode = firstPayment.path("payment_method")
                        .path("transaction_security")
                        .path("url");

                if (!securityUrlNode.isMissingNode() && !securityUrlNode.isNull()) {
                    challengeUrl = securityUrlNode.asString();
                }
            }

            return new CartaoPagamentoResponse(
                    pedido.getId(),
                    pedido.getTotal(),
                    transactionId,
                    orderStatus,
                    challengeUrl,
                    "Processamento iniciado: " + orderStatusDetail
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar Cartão no Mercado Pago (Orders API): " + e.getMessage(), e);
        }
    }
}