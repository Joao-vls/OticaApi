package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.dto.PixPagamentoResponse;
import br.com.otica.otica_loja.enums.StatusPedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
public class CriarPagamentoPixUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public PixPagamentoResponse criarPagamento(UUID pedidoId, UUID usuarioId, String emailCliente) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        // 🎯 1. Validação de Segurança: Garante que o usuarioId pertence ao dono do pedido
        if (usuarioId != null && !pedido.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("O usuário informado não pertence a este pedido.");
        }

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido não está aguardando pagamento.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            // 2. Montando o Body
            Map<String, Object> body = new HashMap<>();
            body.put("type", "online");
            body.put("external_reference", pedido.getId().toString());
            body.put("processing_mode", "automatic");
            body.put("total_amount", String.valueOf(pedido.getTotal()));
            body.put("description", "Pedido Ótica - " + pedido.getId());

            Map<String, Object> payer = new HashMap<>();
            payer.put("email", emailCliente);
            body.put("payer", payer);

            Map<String, Object> paymentMethod = new HashMap<>();
            paymentMethod.put("id", "pix");
            paymentMethod.put("type", "bank_transfer");

            Map<String, Object> paymentItem = new HashMap<>();
            paymentItem.put("amount", String.valueOf(pedido.getTotal()));
            paymentItem.put("payment_method", paymentMethod);
            paymentItem.put("expiration_time", "PT24H");

            Map<String, Object> transactions = new HashMap<>();
            transactions.put("payments", List.of(paymentItem));

            body.put("transactions", transactions);

            // 3. Configurando Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // 4. Executando a chamada
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/orders",
                    entity,
                    String.class
            );

            JsonNode root = mapper.readTree(response.getBody());

            // 🎯 Usando asText() no lugar de asString()
            String orderIdMercadoPago = root.path("id").asString(null);

            // 5. Extraindo os dados do PIX
            JsonNode firstPayment = root.path("transactions").path("payments").get(0);
            JsonNode methodResponse = firstPayment.path("payment_method");

            String chavePixCopiaECola = methodResponse.path("qr_code").asString("");
            String qrCodeBase64 = methodResponse.path("qr_code_base64").asString("");

            // 6. Atualiza e salva o Pedido
            pedido.setOrderIdMercadoPago(orderIdMercadoPago);
            pedido.setStatus(StatusPedido.PROCESSANDO);
            pedido.setAtualizadoEm(OffsetDateTime.now());
            pedidoRepository.save(pedido);

            return new PixPagamentoResponse(
                    pedido.getId(),
                    pedido.getTotal(),
                    chavePixCopiaECola,
                    qrCodeBase64,
                    "Pix gerado com sucesso (Orders API)"
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Pix no Mercado Pago (Orders API): " + e.getMessage(), e);
        }
    }
}