package br.com.otica.otica_loja.Repository.Atendimento;

import br.com.otica.otica_loja.Entity.Atendimento.TicketHistorico;
import br.com.otica.otica_loja.Entity.Atendimento.Ticket;
import br.com.otica.otica_loja.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketHistoricoRepository extends JpaRepository<TicketHistorico, UUID> {

    // Buscar histórico de um ticket específico
    List<TicketHistorico> findByTicket(Ticket ticket);

    // Buscar histórico de um ticket ordenado por data de criação
    List<TicketHistorico> findByTicketOrderByCriadoEmAsc(Ticket ticket);

    // Buscar histórico filtrando por status
    List<TicketHistorico> findByTicketAndStatus(Ticket ticket, TicketStatus status);
}
