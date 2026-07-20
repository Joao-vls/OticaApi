package br.com.otica.otica_loja.Entity.Admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "logs_acesso", schema = "admin")
public class LogAcesso {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId; // FK para auth.users

    @Column(length = 100)
    private String ip;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String rota;

    @Column(length = 20)
    private String metodo;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}
