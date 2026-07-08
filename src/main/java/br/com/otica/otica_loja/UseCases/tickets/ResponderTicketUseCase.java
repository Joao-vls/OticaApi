package br.com.otica.otica_loja.UseCases.tickets;

import br.com.otica.otica_loja.Entity.Atendimento.Ticket;
import br.com.otica.otica_loja.Entity.Atendimento.TicketMensagem;
import br.com.otica.otica_loja.Entity.Atendimento.TicketHistorico;
import br.com.otica.otica_loja.Repository.Atendimento.TicketRepository;
import br.com.otica.otica_loja.Repository.Atendimento.TicketMensagemRepository;
import br.com.otica.otica_loja.Repository.Atendimento.TicketHistoricoRepository;
import br.com.otica.otica_loja.enums.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ResponderTicketUseCase {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketMensagemRepository mensagemRepository;

    @Autowired
    private TicketHistoricoRepository historicoRepository;

    /**
     * Adiciona uma resposta a um ticket existente.
     */
    public TicketMensagem responderTicket(UUID ticketId, UUID usuarioId, String mensagem, String anexoPath) {
        // 1. Buscar ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado."));

        if (ticket.getStatus() == TicketStatus.FECHADO) {
            throw new IllegalStateException("Não é possível responder a um ticket fechado.");
        }

        // 2. Criar mensagem
        TicketMensagem ticketMensagem = new TicketMensagem();
        ticketMensagem.setTicket(ticket);
        ticketMensagem.setUsuarioId(usuarioId);
        ticketMensagem.setMensagem(mensagem);
        ticketMensagem.setAnexoPath(anexoPath);
        ticketMensagem.setCriadoEm(OffsetDateTime.now());

        TicketMensagem salvo = mensagemRepository.save(ticketMensagem);

        // 3. Atualizar ticket
        ticket.setAtualizadoEm(OffsetDateTime.now());
        if (ticket.getStatus() == TicketStatus.ABERTO) {
            ticket.setStatus(TicketStatus.EM_ATENDIMENTO);
        }
        ticketRepository.save(ticket);

        // 4. Registrar histórico
        TicketHistorico historico = new TicketHistorico();
        historico.setTicket(ticket);
        historico.setStatus(ticket.getStatus());
        historico.setObservacao("Nova resposta adicionada ao ticket.");
        historico.setCriadoEm(OffsetDateTime.now());

        historicoRepository.save(historico);

        return salvo;
    }
}
