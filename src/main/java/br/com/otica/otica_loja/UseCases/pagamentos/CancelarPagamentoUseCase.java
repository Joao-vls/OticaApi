package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.dto.CancelarPagamentoRequest;
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
import java.util.Map;
import java.util.UUID;

@Service
public class CancelarPagamentoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    /**
     * Cancela ou estorna o pagamento de um pedido, integrando com a API de Orders do Mercado Pago.
     */
    public Pedido cancelarPagamento(CancelarPagamentoRequest request) {
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        // 2. Validar status atual
        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.ESTORNADO) {
            throw new IllegalArgumentException("Pedido já está cancelado/estornado.");
        }

        // 3. Lógica de Estorno no Mercado Pago
        if (request.estorno() && pedido.getStatus() == StatusPedido.PAGO) {

            // Prioriza o idPagamentoMercadoPago enviado na request, utilizando o salvo na entidade como fallback
            String orderIdMercadoPago = request.idPagamentoMercadoPago() != null && !request.idPagamentoMercadoPago().isBlank()
                    ? request.idPagamentoMercadoPago()
                    : pedido.getOrderIdMercadoPago();

            if (orderIdMercadoPago == null || orderIdMercadoPago.isBlank()) {
                throw new IllegalArgumentException("ID da Order do Mercado Pago (ex: ORD...) não encontrado para estorno.");
            }

            try {
                RestTemplate restTemplate = new RestTemplate();
                ObjectMapper mapper = new ObjectMapper();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(accessToken);
                headers.set("X-Idempotency-Key", UUID.randomUUID().toString());

                // Body vazio = Full Refund (estorna o valor total da order)
                Map<String, Object> body = new HashMap<>();
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

                // POST /v1/orders/{order_id}/refund
                ResponseEntity<String> response = restTemplate.postForEntity(
                        "https://api.mercadopago.com/v1/orders/" + orderIdMercadoPago + "/refund",
                        entity,
                        String.class
                );

                JsonNode root = mapper.readTree(response.getBody());

                // Extrai o ID de reembolso devolvido pela API de Refund ("REF...")
                String refundIdMP = "N/A";
                JsonNode refundsArray = root.path("transactions").path("refunds");
                if (refundsArray.isArray() && !refundsArray.isEmpty()) {
                    refundIdMP = refundsArray.get(0).path("id").asString();
                }

                pedido.setStatus(StatusPedido.ESTORNADO);
                pedido.setObservacoes("Pagamento estornado (Ref MP: " + refundIdMP + ") | Usuário: " + request.usuarioId() + " | Motivo: " + request.motivo());

            } catch (Exception e) {
                throw new RuntimeException("Falha ao processar estorno no Mercado Pago (Orders API): " + e.getMessage(), e);
            }

        } else {
            // Cancelamento simples (pedidos pendentes, boleto ou pix expirado/desistido)
            pedido.setStatus(StatusPedido.CANCELADO);
            pedido.setObservacoes("Pagamento cancelado | Usuário: " + request.usuarioId() + " | Motivo: " + request.motivo());
        }

        pedido.setAtualizadoEm(OffsetDateTime.now());

        // 4. Persistir alterações
        return pedidoRepository.save(pedido);
    }
}