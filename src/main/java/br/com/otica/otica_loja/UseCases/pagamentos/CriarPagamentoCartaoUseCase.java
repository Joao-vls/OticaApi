package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CriarPagamentoCartaoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Cria um pagamento via cartão para um pedido.
     */
    public CartaoPagamentoResponse criarPagamento(UUID pedidoId, UUID usuarioId, CartaoPagamentoRequest request) {
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido não está aguardando pagamento.");
        }

        // 2. Simular integração com gateway de pagamento
        boolean autorizado = autorizarPagamento(request, pedido.getTotal());

        if (!autorizado) {
            throw new IllegalArgumentException("Pagamento não autorizado pelo banco.");
        }

        // 3. Atualizar pedido para status PROCESSANDO (aguardando confirmação da operadora)
        pedido.setStatus(StatusPedido.PROCESSANDO);
        pedido.setAtualizadoEm(OffsetDateTime.now());
        pedidoRepository.save(pedido);

        // 4. Retornar resposta com dados do pagamento
        return new CartaoPagamentoResponse(pedido.getId(), pedido.getTotal(), "Pagamento com cartão autorizado e em processamento");
    }

    /**
     * Método auxiliar para simular autorização de pagamento.
     * Em produção, isso seria integrado com um gateway (ex.: Cielo, Stone, PagSeguro).
     */
    private boolean autorizarPagamento(CartaoPagamentoRequest request, BigDecimal valor) {
        // Simulação: autoriza se número do cartão termina em par
        return request.getNumeroCartao().endsWith("2") || request.getNumeroCartao().endsWith("4")
                || request.getNumeroCartao().endsWith("6") || request.getNumeroCartao().endsWith("8");
    }

    // Classe auxiliar para requisição
    public static class CartaoPagamentoRequest {
        private String numeroCartao;
        private String nomeTitular;
        private String validade;
        private String cvv;

        public CartaoPagamentoRequest(String numeroCartao, String nomeTitular, String validade, String cvv) {
            this.numeroCartao = numeroCartao;
            this.nomeTitular = nomeTitular;
            this.validade = validade;
            this.cvv = cvv;
        }

        public String getNumeroCartao() {
            return numeroCartao;
        }

        public String getNomeTitular() {
            return nomeTitular;
        }

        public String getValidade() {
            return validade;
        }

        public String getCvv() {
            return cvv;
        }
    }

    // Classe auxiliar para resposta
    public static class CartaoPagamentoResponse {
        private UUID pedidoId;
        private BigDecimal valor;
        private String mensagem;

        public CartaoPagamentoResponse(UUID pedidoId, BigDecimal valor, String mensagem) {
            this.pedidoId = pedidoId;
            this.valor = valor;
            this.mensagem = mensagem;
        }

        public UUID getPedidoId() {
            return pedidoId;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public String getMensagem() {
            return mensagem;
        }
    }
}
