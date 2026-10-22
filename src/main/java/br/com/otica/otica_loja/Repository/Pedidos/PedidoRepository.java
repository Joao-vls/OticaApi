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

    Optional<Pedido> findByNumero(Long numero);
    Page<Pedido> findByStatusIn(List<StatusPedido> status, Pageable pageable);
    List<Pedido> findByUsuarioId(UUID usuarioId);
    List<Pedido> findByStatus(StatusPedido status);
    List<Pedido> findByTotalGreaterThan(BigDecimal valor);
    List<Pedido> findByCriadoEmAfter(OffsetDateTime data);
    List<Pedido> findByAtualizadoEmAfter(OffsetDateTime data);
    List<Pedido> findByCupomIsNotNull();
    boolean existsByNumero(Long numero);

    // 🎯 CORREÇÃO DO FILTRO: Usando SpEL para evitar erro de NULL com Enums no Hibernate
    @Query("SELECT p FROM Pedido p WHERE " +
            "(:#{#status == null} = true OR p.status = :status) AND " +
            "(:#{#busca == null || #busca.trim().isEmpty()} = true OR " +
            "LOWER(CAST(p.id AS string)) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(CAST(p.numero AS string)) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(CAST(p.usuarioId AS string)) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(COALESCE(p.clienteNome, '')) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(COALESCE(p.clienteCpf, '')) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(COALESCE(p.clienteTelefone, '')) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(COALESCE(p.clienteEmail, '')) LIKE LOWER(CONCAT('%', :busca, '%')))")
    Page<Pedido> buscarComFiltrosPaginado(
            @Param("busca") String busca,
            @Param("status") StatusPedido status,
            Pageable pageable);

    Optional<Pedido> findTopByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);
}