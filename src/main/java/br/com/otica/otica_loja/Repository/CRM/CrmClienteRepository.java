package br.com.otica.otica_loja.Repository.CRM;

import br.com.otica.otica_loja.Entity.CRM.CrmCliente;
import br.com.otica.otica_loja.enums.NivelCliente;
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
public interface CrmClienteRepository extends JpaRepository<CrmCliente, UUID> {

    // Buscar cliente CRM pelo usuárioId
    Optional<CrmCliente> findByUsuarioId(UUID usuarioId);

    // Buscar clientes por nível (Bronze, Prata, Ouro, Diamante)
    List<CrmCliente> findByNivel(NivelCliente nivel);

    // Buscar clientes com score acima de um valor
    List<CrmCliente> findByScoreGreaterThan(Integer score);

    // Buscar clientes com valor gasto acima de um limite
    List<CrmCliente> findByValorGastoGreaterThan(BigDecimal valor);

    // Buscar clientes com total de pedidos acima de um limite
    List<CrmCliente> findByTotalPedidosGreaterThan(Integer totalPedidos);

    // Buscar clientes que fizeram pedido recentemente
    List<CrmCliente> findByUltimoPedidoEmAfter(OffsetDateTime data);

    // Buscar clientes que não fazem pedido há muito tempo
    List<CrmCliente> findByUltimoPedidoEmBefore(OffsetDateTime data);

    public interface NivelProjection {
        NivelCliente getNivel();
        Long getQuantidade();
    }

    @Query("SELECT c.nivel as nivel, COUNT(c.id) as quantidade FROM CrmCliente c GROUP BY c.nivel")
    List<NivelProjection> agruparPorNivel();

    @Query("SELECT COUNT(c.id) FROM CrmCliente c WHERE c.score > :limiteInferior AND c.score <= :limiteSuperior")
    long contarPorFaixaDeScore(@Param("limiteInferior") int limiteInferior, @Param("limiteSuperior") int limiteSuperior);

    @Query("SELECT COUNT(c.id) FROM CrmCliente c WHERE c.score > :limiteInferior")
    long contarPorScoreAcimaDe(@Param("limiteInferior") int limiteInferior);

    @Query("SELECT COUNT(c.id) FROM CrmCliente c WHERE c.ultimoPedidoEm >= :data")
    long contarAtivosRecentes(@Param("data") OffsetDateTime data);

    @Query("SELECT COUNT(c.id) FROM CrmCliente c WHERE c.ultimoPedidoEm < :dataCorteRisco AND c.ultimoPedidoEm >= :dataCorteInativo")
    long contarEmRisco(@Param("dataCorteRisco") OffsetDateTime dataCorteRisco, @Param("dataCorteInativo") OffsetDateTime dataCorteInativo);

    @Query("SELECT COUNT(c.id) FROM CrmCliente c WHERE c.ultimoPedidoEm < :dataCorteInativo OR c.ultimoPedidoEm IS NULL")
    long contarInativos(@Param("dataCorteInativo") OffsetDateTime dataCorteInativo);
}
