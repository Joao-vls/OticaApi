package br.com.otica.otica_loja.Repository.Atendimento;

import br.com.otica.otica_loja.Entity.Atendimento.TicketMensagem;
import br.com.otica.otica_loja.Entity.Atendimento.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketMensagemRepository extends JpaRepository<TicketMensagem, UUID> {

    // Buscar todas as mensagens de um ticket
    List<TicketMensagem> findByTicket(Ticket ticket);

    // Buscar mensagens de um ticket ordenadas por data de criação
    List<TicketMensagem> findByTicketOrderByCriadoEmAsc(Ticket ticket);

    // Buscar mensagens de um usuário dentro de um ticket
    List<TicketMensagem> findByTicketAndUsuarioId(Ticket ticket, UUID usuarioId);

    // Buscar mensagens que possuem anexos
    List<TicketMensagem> findByTicketAndAnexoPathIsNotNull(Ticket ticket);
}
