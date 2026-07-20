package br.com.otica.otica_loja.Entity.CRM;

import br.com.otica.otica_loja.enums.TipoNotificacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "notificacoes", schema = "loja")
public class Notificacao {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId; // FK para auth.users

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoNotificacao tipo;


    @Column(nullable = false)
    private Boolean visualizada = false;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}