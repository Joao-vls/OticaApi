package br.com.otica.otica_loja.Repository.CRM;

import br.com.otica.otica_loja.Entity.CRM.CrmCliente;
import br.com.otica.otica_loja.enums.NivelCliente;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
