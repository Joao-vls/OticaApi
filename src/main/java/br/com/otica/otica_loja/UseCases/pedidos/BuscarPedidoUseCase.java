package br.com.otica.otica_loja.UseCases.pedidos;


import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BuscarPedidoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Buscar pedido pelo ID.
     */
    public Pedido buscarPorId(UUID pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
    }

    /**
     * Buscar pedido pelo número.
     */
    public Pedido buscarPorNumero(Long numero) {
        return pedidoRepository.findByNumero(numero)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
    }

    /**
     * Buscar todos os pedidos de um usuário.
     */
    public List<Pedido> buscarPorUsuario(UUID usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }
}
