package br.com.otica.otica_loja.UseCases.pedidos;

import br.com.otica.otica_loja.Entity.Estoque.EstoqueMovimentacao;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.PedidoItem;
import br.com.otica.otica_loja.Repository.Estoque.EstoqueMovimentacaoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import br.com.otica.otica_loja.enums.TipoEmail;
import br.com.otica.otica_loja.enums.TipoMovimentacao;
import br.com.otica.otica_loja.service.email.EmailService;
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

    @Autowired
    private EmailService emailService; // 👈 Adicionado

    public Pedido cancelar(UUID pedidoId, UUID usuarioId, String observacao) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException("Pedido já está cancelado.");
        }

        List<PedidoItem> itens = pedidoItemRepository.findByPedido(pedido);

        // Devolver estoque
        for (PedidoItem item : itens) {
            ProdutoVariante variante = item.getVariante();
            if (variante != null) {
                Integer saldoAnterior = variante.getStock();
                Integer saldoAtual = saldoAnterior + item.getQuantidade();
                variante.setStock(saldoAtual);

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

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setObservacoes(observacao);
        pedido.setAtualizadoEm(OffsetDateTime.now());

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 👇 DISPARA O E-MAIL DE CANCELAMENTO
        emailService.enviarEmailPedido(pedidoSalvo, TipoEmail.PEDIDO_CANCELADO, null);

        return pedidoSalvo;
    }
}