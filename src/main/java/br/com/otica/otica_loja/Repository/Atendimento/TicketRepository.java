package br.com.otica.otica_loja.Repository.Atendimento;

import br.com.otica.otica_loja.Entity.Atendimento.Ticket;
import br.com.otica.otica_loja.enums.TicketPrioridade;
import br.com.otica.otica_loja.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    // Buscar ticket pelo protocolo
    Optional<Ticket> findByProtocolo(String protocolo);

    // Buscar tickets de um usuário
    List<Ticket> findByUsuarioId(UUID usuarioId);

    // Buscar tickets por status
    List<Ticket> findByStatus(TicketStatus status);

    // Buscar tickets por prioridade
    List<Ticket> findByPrioridade(TicketPrioridade prioridade);

    // Buscar tickets de um usuário com determinado status
    List<Ticket> findByUsuarioIdAndStatus(UUID usuarioId, TicketStatus status);
}
