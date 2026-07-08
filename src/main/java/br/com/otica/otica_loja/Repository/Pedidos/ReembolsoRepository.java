package br.com.otica.otica_loja.Repository.Pedidos;

import br.com.otica.otica_loja.Entity.Pedidos.Reembolso;
import br.com.otica.otica_loja.Entity.Pedidos.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReembolsoRepository extends JpaRepository<Reembolso, UUID> {

    // Buscar reembolsos de um pagamento específico
    List<Reembolso> findByPagamento(Pagamento pagamento);

    // Buscar reembolsos acima de um valor
    List<Reembolso> findByValorGreaterThan(BigDecimal valor);

    // Buscar reembolsos criados após uma data
    List<Reembolso> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar reembolsos criados antes de uma data
    List<Reembolso> findByCriadoEmBefore(OffsetDateTime data);

    // Buscar reembolsos por motivo (texto parcial)
    List<Reembolso> findByMotivoContainingIgnoreCase(String motivo);
}
