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
public class CriarPagamentoBoletoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Cria um pagamento via boleto para um pedido.
     */
    public BoletoPagamentoResponse criarPagamento(UUID pedidoId, UUID usuarioId) {
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido não está aguardando pagamento.");
        }

        // 2. Gerar código de barras e linha digitável (simulação)
        String codigoBarras = gerarCodigoBarras(pedido);
        String linhaDigitavel = gerarLinhaDigitavel(pedido);

        // 3. Atualizar pedido para status PROCESSANDO (aguardando compensação do boleto)
        pedido.setStatus(StatusPedido.PROCESSANDO);
        pedido.setAtualizadoEm(OffsetDateTime.now());
        pedidoRepository.save(pedido);

        // 4. Retornar resposta com dados do boleto
        return new BoletoPagamentoResponse(pedido.getId(), pedido.getTotal(), codigoBarras, linhaDigitavel, "Boleto gerado com sucesso");
    }

    /**
     * Método auxiliar para gerar código de barras (simulação).
     */
    private String gerarCodigoBarras(Pedido pedido) {
        return "34191.79001 01043.510047 91020.150008 5 123400000" + pedido.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Método auxiliar para gerar linha digitável (simulação).
     */
    private String gerarLinhaDigitavel(Pedido pedido) {
        return "34191.79001 01043.510047 91020.150008 5 123400000" + pedido.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Classe auxiliar para resposta
    public static class BoletoPagamentoResponse {
        private UUID pedidoId;
        private BigDecimal valor;
        private String codigoBarras;
        private String linhaDigitavel;
        private String mensagem;

        public BoletoPagamentoResponse(UUID pedidoId, BigDecimal valor, String codigoBarras, String linhaDigitavel, String mensagem) {
            this.pedidoId = pedidoId;
            this.valor = valor;
            this.codigoBarras = codigoBarras;
            this.linhaDigitavel = linhaDigitavel;
            this.mensagem = mensagem;
        }

        public UUID getPedidoId() {
            return pedidoId;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public String getCodigoBarras() {
            return codigoBarras;
        }

        public String getLinhaDigitavel() {
            return linhaDigitavel;
        }

        public String getMensagem() {
            return mensagem;
        }
    }
}
