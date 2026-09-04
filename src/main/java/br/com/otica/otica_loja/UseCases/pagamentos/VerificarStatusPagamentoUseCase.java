package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VerificarStatusPagamentoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    public String verificarStatus(UUID pedidoId, UUID usuarioId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (usuarioId != null && !pedido.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("Acesso negado a este pedido.");
        }

        // Retorna o status atual do pedido em texto para o front-end
        return pedido.getStatus().name();
    }
}