package br.com.otica.otica_loja.UseCases.tickets;

import br.com.otica.otica_loja.Entity.Atendimento.Ticket;
import br.com.otica.otica_loja.Repository.Atendimento.TicketRepository;
import br.com.otica.otica_loja.enums.TicketPrioridade;
import br.com.otica.otica_loja.enums.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarTicketsUseCase {

    @Autowired
    private TicketRepository ticketRepository;

    /**
     * Lista todos os tickets do sistema.
     */
    public List<Ticket> listarTodos() {
        return ticketRepository.findAll();
    }

    /**
     * Lista tickets por status.
     */
    public List<Ticket> listarPorStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    /**
     * Lista tickets por prioridade.
     */
    public List<Ticket> listarPorPrioridade(TicketPrioridade prioridade) {
        return ticketRepository.findByPrioridade(prioridade);
    }

    /**
     * Lista tickets de um usuário específico.
     */
    public List<Ticket> listarPorUsuario(UUID usuarioId) {
        return ticketRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Lista tickets de um usuário com determinado status.
     */
    public List<Ticket> listarPorUsuarioEStatus(UUID usuarioId, TicketStatus status) {
        return ticketRepository.findByUsuarioIdAndStatus(usuarioId, status);
    }
}
