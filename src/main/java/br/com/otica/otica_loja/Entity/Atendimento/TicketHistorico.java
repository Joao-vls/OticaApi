package br.com.otica.otica_loja.Entity.Atendimento;

import br.com.otica.otica_loja.enums.TicketStatus;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import necessário
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "ticket_historico", schema = "loja")
public class TicketHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Bloqueia a serialização para evitar erro de Lazy Loading ou Loop
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TicketStatus status;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();
}