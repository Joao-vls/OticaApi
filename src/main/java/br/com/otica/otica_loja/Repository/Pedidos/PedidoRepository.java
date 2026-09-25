package br.com.otica.otica_loja.Repository.Pedidos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    // 1. Projeção para os totais do pedido
    interface ResumoFinanceiroProjection {
        Long getQuantidade();
        java.math.BigDecimal getReceitaTotal();
        java.math.BigDecimal getTotalDescontos();
        java.math.BigDecimal getTotalFrete();
    }

    @Query("SELECT COUNT(p.id) as quantidade, " +
            "SUM(p.total) as receitaTotal, " +
            "SUM(p.desconto) as totalDescontos, " +
            "SUM(p.frete) as totalFrete " +
            "FROM Pedido p " +
            "WHERE p.status IN :statusValidos " +
            "AND p.criadoEm >= :inicio AND p.criadoEm <= :fim")
    ResumoFinanceiroProjection obterResumoFinanceiroNoPeriodo(
            @Param("statusValidos") List<StatusPedido> statusValidos,
            @Param("inicio") OffsetDateTime inicio,
            @Param("fim") OffsetDateTime fim);
    // Buscar pedido pelo número
    Optional<Pedido> findByNumero(Long numero);

    Page<Pedido> findByStatusIn(List<StatusPedido> status, Pageable pageable);
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

    // 🔴 NOVO: Buscar o último pedido realizado pelo usuário ordenado por data descendente
    Optional<Pedido> findTopByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);
}