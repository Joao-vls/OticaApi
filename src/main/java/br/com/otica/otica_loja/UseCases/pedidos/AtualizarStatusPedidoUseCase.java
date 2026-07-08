package br.com.otica.otica_loja.UseCases.pedidos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.PedidoStatusHistorico;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoStatusHistoricoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AtualizarStatusPedidoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoStatusHistoricoRepository historicoRepository;

    /**
     * Atualiza o status de um pedido e registra no histórico.
     */
    public Pedido atualizarStatus(UUID pedidoId, StatusPedido novoStatus, UUID usuarioId, String observacao) {
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        // 2. Verificar se já está no status desejado
        if (pedido.getStatus() == novoStatus) {
            throw new IllegalArgumentException("Pedido já está com o status " + novoStatus);
        }

        // 3. Atualizar status
        pedido.setStatus(novoStatus);
        if (observacao != null && !observacao.isBlank()) {
            pedido.setObservacoes(observacao);
        }
        pedido.setAtualizadoEm(OffsetDateTime.now());

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        // 4. Registrar histórico
        PedidoStatusHistorico historico = new PedidoStatusHistorico();
        historico.setPedido(pedidoAtualizado);
        historico.setStatus(novoStatus.name());
        historico.setObservacao(observacao);
        historico.setCriadoEm(OffsetDateTime.now());

        historicoRepository.save(historico);

        return pedidoAtualizado;
    }
}
