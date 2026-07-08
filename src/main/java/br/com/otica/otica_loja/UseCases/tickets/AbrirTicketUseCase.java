package br.com.otica.otica_loja.UseCases.tickets;

import br.com.otica.otica_loja.Entity.Atendimento.Ticket;
import br.com.otica.otica_loja.Entity.Atendimento.TicketHistorico;
import br.com.otica.otica_loja.Repository.Atendimento.TicketRepository;
import br.com.otica.otica_loja.Repository.Atendimento.TicketHistoricoRepository;
import br.com.otica.otica_loja.enums.TicketPrioridade;
import br.com.otica.otica_loja.enums.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AbrirTicketUseCase {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketHistoricoRepository historicoRepository;

    /**
     * Cria um novo ticket de suporte.
     */
    public Ticket abrirTicket(UUID usuarioId, String assunto, String descricao, TicketPrioridade prioridade) {
        // 1. Criar ticket
        Ticket ticket = new Ticket();
        ticket.setUsuarioId(usuarioId);
        ticket.setAssunto(assunto);
        ticket.setDescricao(descricao);
        ticket.setPrioridade(prioridade != null ? prioridade : TicketPrioridade.NORMAL);
        ticket.setStatus(TicketStatus.ABERTO);
        ticket.setCriadoEm(OffsetDateTime.now());
        ticket.setAtualizadoEm(OffsetDateTime.now());

        // Gerar protocolo único (exemplo simples: TCK-<UUID curto>)
        ticket.setProtocolo("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Ticket salvo = ticketRepository.save(ticket);

        // 2. Registrar histórico inicial
        TicketHistorico historico = new TicketHistorico();
        historico.setTicket(salvo);
        historico.setStatus(TicketStatus.ABERTO);
        historico.setObservacao("Ticket criado com prioridade " + salvo.getPrioridade());
        historico.setCriadoEm(OffsetDateTime.now());

        historicoRepository.save(historico);

        return salvo;
    }
}
