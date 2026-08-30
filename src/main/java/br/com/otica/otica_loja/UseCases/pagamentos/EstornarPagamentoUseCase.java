package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.Reembolso;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.ReembolsoRepository;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EstornarPagamentoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ReembolsoRepository reembolsoRepository;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    /**
     * @param pedidoId ID interno do banco
     * @param orderIdMercadoPago ID da Order ("ORD..."). Se for nulo/vazio, utiliza o salvo no Pedido.
     * @param transactionIdMercadoPago ID do pagamento ("PAY...") para reembolso parcial
     * @param valor Valor a estornar (nulo se for estorno total)
     * @param usuarioId ID do Admin/Atendente solicitando
     * @param motivo Motivo do estorno
     */
    public Reembolso estornarPagamento(UUID pedidoId, String orderIdMercadoPago, String transactionIdMercadoPago, BigDecimal valor, UUID usuarioId, String motivo) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.PAGO) {
            throw new IllegalArgumentException("Somente pedidos pagos podem ser estornados.");
        }

        // 🎯 Fallback: se não informado no DTO, utiliza a Order ID salva no pedido
        String orderIdFinal = (orderIdMercadoPago != null && !orderIdMercadoPago.isBlank())
                ? orderIdMercadoPago
                : pedido.getOrderIdMercadoPago();

        if (orderIdFinal == null || orderIdFinal.isBlank()) {
            throw new IllegalArgumentException("ID da Order do Mercado Pago não encontrado para estorno.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

            Map<String, Object> body = new HashMap<>();

            // Se for estorno parcial de uma transação específica:
            if (transactionIdMercadoPago != null && !transactionIdMercadoPago.isBlank() && valor != null) {
                Map<String, Object> transactionToRefund = new HashMap<>();
                transactionToRefund.put("id", transactionIdMercadoPago);
                transactionToRefund.put("amount", valor.toString());

                body.put("transactions", List.of(transactionToRefund));
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // POST /v1/orders/{order_id}/refund
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/orders/" + orderIdFinal + "/refund",
                    entity,
                    String.class
            );

            JsonNode root = mapper.readTree(response.getBody());

            // Extrai o ID do reembolso "REF..." devolvido pela API
            String refundIdMP = "N/A";
            JsonNode refundsArray = root.path("transactions").path("refunds");
            if (refundsArray.isArray() && !refundsArray.isEmpty()) {
                refundIdMP = refundsArray.get(0).path("id").asString();
            }

            String statusDetail = root.path("status_detail").asString();
            StatusPedido novoStatus = "partially_refunded".equals(statusDetail) ? StatusPedido.PROCESSANDO : StatusPedido.ESTORNADO;

            // Define o valor do registro: se foi total (valor == null), salva o subtotal/total do pedido
            BigDecimal valorReembolso = (valor != null) ? valor : pedido.getTotal();

            // Persiste o Reembolso
            Reembolso reembolso = new Reembolso();
            reembolso.setValor(valorReembolso);
            reembolso.setMotivo(motivo + " | MP Refund ID: " + refundIdMP);
            reembolso.setCriadoEm(OffsetDateTime.now());
            reembolsoRepository.save(reembolso);

            // Atualiza o Pedido
            pedido.setStatus(novoStatus);
            pedido.setAtualizadoEm(OffsetDateTime.now());
            pedido.setObservacoes("Estorno (" + statusDetail + ") | Usuário admin: " + usuarioId);
            pedidoRepository.save(pedido);

            return reembolso;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao solicitar estorno no Mercado Pago (Orders API): " + e.getMessage(), e);
        }
    }
}