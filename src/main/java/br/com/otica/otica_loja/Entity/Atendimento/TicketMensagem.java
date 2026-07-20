package br.com.otica.otica_loja.Entity.Atendimento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "ticket_mensagens", schema = "loja")
public class TicketMensagem {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "usuario_id")
    private UUID usuarioId; // FK para auth.users

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "anexo_path", columnDefinition = "TEXT")
    private String anexoPath;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}