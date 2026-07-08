package br.com.otica.otica_loja.UseCases.pagamento;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CriarPagamentoPixUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Cria um pagamento via Pix para um pedido.
     */
    public PixPagamentoResponse criarPagamento(UUID pedidoId, UUID usuarioId) {
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido não está aguardando pagamento.");
        }

        // 2. Gerar chave Pix dinâmica (exemplo simplificado)
        String chavePix = gerarChavePix(pedido);

        // 3. Atualizar pedido para status PROCESSANDO (aguardando confirmação do pagamento)
        pedido.setStatus(StatusPedido.PROCESSANDO);
        pedido.setAtualizadoEm(OffsetDateTime.now());
        pedidoRepository.save(pedido);

        // 4. Retornar resposta com dados do pagamento Pix
        return new PixPagamentoResponse(pedido.getId(), pedido.getTotal(), chavePix, "Pagamento Pix gerado com sucesso");
    }

    /**
     * Método auxiliar para gerar chave Pix dinâmica.
     * Em produção, isso seria integrado com o PSP (Banco Central ou provedor).
     */
    private String gerarChavePix(Pedido pedido) {
        return "00020126580014BR.GOV.BCB.PIX0114+5588999999995204000053039865405"
                + pedido.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP)
                + "5802BR5920OTICA LOJA LTDA6009MINAS62070503***6304ABCD";
    }

    // Classe auxiliar para resposta
    public static class PixPagamentoResponse {
        private UUID pedidoId;
        private BigDecimal valor;
        private String chavePix;
        private String mensagem;

        public PixPagamentoResponse(UUID pedidoId, BigDecimal valor, String chavePix, String mensagem) {
            this.pedidoId = pedidoId;
            this.valor = valor;
            this.chavePix = chavePix;
            this.mensagem = mensagem;
        }

        public UUID getPedidoId() {
            return pedidoId;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public String getChavePix() {
            return chavePix;
        }

        public String getMensagem() {
            return mensagem;
        }
    }
}
