package br.com.otica.otica_loja.UseCases.pedidos;


import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarPedidosUsuarioUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Lista todos os pedidos de um usuário.
     */
    public List<Pedido> listarTodos(UUID usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Lista pedidos de um usuário filtrando por status.
     */
    public List<Pedido> listarPorStatus(UUID usuarioId, StatusPedido status) {
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .filter(p -> p.getStatus() == status)
                .toList();
    }
}
