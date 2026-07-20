package br.com.otica.otica_loja.Entity.Auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "sessoes", schema = "loja")
public class Sessao {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId; // FK para auth.users

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "expira_em", nullable = false)
    private OffsetDateTime expiraEm;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

}
