package br.com.otica.otica_loja.Entity.Atendimento;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import br.com.otica.otica_loja.enums.TicketPrioridade;
import br.com.otica.otica_loja.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "tickets", schema = "loja")
public class Ticket {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, length = 50)
    private String protocolo;

    @Column(name = "usuario_id")
    private UUID usuarioId; // FK para auth.users

    @Column(nullable = false)
    private String assunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketPrioridade prioridade = TicketPrioridade.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status = TicketStatus.ABERTO;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

}
