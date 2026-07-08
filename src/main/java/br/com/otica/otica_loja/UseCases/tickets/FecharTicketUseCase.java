package br.com.otica.otica_loja.UseCases.tickets;

import br.com.otica.otica_loja.Entity.Atendimento.Ticket;
import br.com.otica.otica_loja.Entity.Atendimento.TicketHistorico;
import br.com.otica.otica_loja.Repository.Atendimento.TicketRepository;
import br.com.otica.otica_loja.Repository.Atendimento.TicketHistoricoRepository;
import br.com.otica.otica_loja.enums.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class FecharTicketUseCase {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketHistoricoRepository historicoRepository;

    /**
     * Fecha um ticket de suporte.
     */
    public Ticket fecharTicket(UUID ticketId, String observacao) {
        // 1. Buscar ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado."));

        if (ticket.getStatus() == TicketStatus.FECHADO) {
            throw new IllegalStateException("O ticket já está fechado.");
        }

        // 2. Atualizar status e data
        ticket.setStatus(TicketStatus.FECHADO);
        ticket.setAtualizadoEm(OffsetDateTime.now());

        Ticket salvo = ticketRepository.save(ticket);

        // 3. Registrar histórico
        TicketHistorico historico = new TicketHistorico();
        historico.setTicket(salvo);
        historico.setStatus(TicketStatus.FECHADO);
        historico.setObservacao(observacao != null ? observacao : "Ticket encerrado.");
        historico.setCriadoEm(OffsetDateTime.now());

        historicoRepository.save(historico);

        return salvo;
    }
}
