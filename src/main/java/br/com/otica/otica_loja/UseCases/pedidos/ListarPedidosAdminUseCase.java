package br.com.otica.otica_loja.UseCases.pedidos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ListarPedidosAdminUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Lista todos os pedidos do sistema.
     */
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    /**
     * Lista pedidos filtrando por status.
     */
    public List<Pedido> listarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }

    /**
     * Lista pedidos com total acima de um valor.
     */
    public List<Pedido> listarPorValorMinimo(BigDecimal valor) {
        return pedidoRepository.findByTotalGreaterThan(valor);
    }

    /**
     * Lista pedidos criados após uma data.
     */
    public List<Pedido> listarCriadosDepois(OffsetDateTime data) {
        return pedidoRepository.findByCriadoEmAfter(data);
    }

    /**
     * Lista pedidos atualizados após uma data.
     */
    public List<Pedido> listarAtualizadosDepois(OffsetDateTime data) {
        return pedidoRepository.findByAtualizadoEmAfter(data);
    }

    /**
     * Lista pedidos que tiveram cupom aplicado.
     */
    public List<Pedido> listarComCupom() {
        return pedidoRepository.findByCupomIsNotNull();
    }
}
