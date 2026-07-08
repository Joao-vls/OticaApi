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
public class BuscarTicketUseCase {

    @Autowired
    private TicketRepository ticketRepository;

    /**
     * Buscar ticket por ID.
     */
    public Ticket buscarPorId(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado."));
    }

    /**
     * Buscar ticket por protocolo.
     */
    public Ticket buscarPorProtocolo(String protocolo) {
        return ticketRepository.findByProtocolo(protocolo)
                .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado."));
    }

    /**
     * Listar todos os tickets de um usuário.
     */
    public List<Ticket> listarPorUsuario(UUID usuarioId) {
        return ticketRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Listar tickets por status.
     */
    public List<Ticket> listarPorStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    /**
     * Listar tickets por prioridade.
     */
    public List<Ticket> listarPorPrioridade(TicketPrioridade prioridade) {
        return ticketRepository.findByPrioridade(prioridade);
    }

    /**
     * Listar tickets de um usuário com determinado status.
     */
    public List<Ticket> listarPorUsuarioEStatus(UUID usuarioId, TicketStatus status) {
        return ticketRepository.findByUsuarioIdAndStatus(usuarioId, status);
    }
}
