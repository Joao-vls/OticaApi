package br.com.otica.otica_loja.UseCases.pedidos;


import br.com.otica.otica_loja.Entity.Estoque.EstoqueMovimentacao;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.PedidoItem;
import br.com.otica.otica_loja.Repository.Estoque.EstoqueMovimentacaoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import br.com.otica.otica_loja.enums.TipoMovimentacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CancelarPedidoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    @Autowired
    private EstoqueMovimentacaoRepository estoqueMovimentacaoRepository;

    /**
     * Cancela um pedido e devolve os itens ao estoque.
     */
    public Pedido cancelar(UUID pedidoId, UUID usuarioId, String observacao) {
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException("Pedido já está cancelado.");
        }

        // 2. Buscar itens do pedido
        List<PedidoItem> itens = pedidoItemRepository.findByPedido(pedido);

        // 3. Devolver estoque
        for (PedidoItem item : itens) {
            ProdutoVariante variante = item.getVariante();
            if (variante != null) {
                Integer saldoAnterior = variante.getStock();
                Integer saldoAtual = saldoAnterior + item.getQuantidade();
                variante.setStock(saldoAtual);

                // Registrar movimentação de estoque
                EstoqueMovimentacao movimentacao = new EstoqueMovimentacao();
                movimentacao.setVariante(variante);
                movimentacao.setTipo(TipoMovimentacao.ENTRADA);
                movimentacao.setQuantidade(item.getQuantidade());
                movimentacao.setSaldoAnterior(saldoAnterior);
                movimentacao.setSaldoAtual(saldoAtual);
                movimentacao.setUsuarioId(usuarioId);
                movimentacao.setObservacao("Cancelamento do pedido " + pedido.getNumero());
                movimentacao.setCriadoEm(OffsetDateTime.now());

                estoqueMovimentacaoRepository.save(movimentacao);
            }
        }

        // 4. Atualizar status do pedido
        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setObservacoes(observacao);
        pedido.setAtualizadoEm(OffsetDateTime.now());

        return pedidoRepository.save(pedido);
    }
}
