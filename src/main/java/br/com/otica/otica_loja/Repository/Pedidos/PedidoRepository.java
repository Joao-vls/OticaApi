package br.com.otica.otica_loja.Repository.Pedidos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    // Buscar pedido pelo número
    Optional<Pedido> findByNumero(Long numero);

    // Buscar pedidos de um usuário
    List<Pedido> findByUsuarioId(UUID usuarioId);

    // Buscar pedidos por status
    List<Pedido> findByStatus(StatusPedido status);

    // Buscar pedidos com total acima de um valor
    List<Pedido> findByTotalGreaterThan(BigDecimal valor);

    // Buscar pedidos criados após uma data
    List<Pedido> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar pedidos atualizados após uma data
    List<Pedido> findByAtualizadoEmAfter(OffsetDateTime data);

    // Buscar pedidos com cupom aplicado
    List<Pedido> findByCupomIsNotNull();

    // Verificar se já existe pedido com determinado número
    boolean existsByNumero(Long numero);
}
