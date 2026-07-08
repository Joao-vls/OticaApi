package br.com.otica.otica_loja.Repository.Pedidos;

import br.com.otica.otica_loja.Entity.Pedidos.PedidoStatusHistorico;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoStatusHistoricoRepository extends JpaRepository<PedidoStatusHistorico, UUID> {

    // Buscar histórico de status de um pedido específico
    List<PedidoStatusHistorico> findByPedido(Pedido pedido);

    // Buscar histórico de status de um pedido ordenado por data
    List<PedidoStatusHistorico> findByPedidoOrderByCriadoEmAsc(Pedido pedido);

    // Buscar histórico por status específico
    List<PedidoStatusHistorico> findByStatus(String status);

    // Buscar histórico criado após uma data
    List<PedidoStatusHistorico> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar histórico criado antes de uma data
    List<PedidoStatusHistorico> findByCriadoEmBefore(OffsetDateTime data);

    // Buscar histórico entre duas datas
    List<PedidoStatusHistorico> findByCriadoEmBetween(OffsetDateTime inicio, OffsetDateTime fim);
}
