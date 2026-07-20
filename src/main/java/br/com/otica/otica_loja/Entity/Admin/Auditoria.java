package br.com.otica.otica_loja.Entity.Admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "auditoria", schema = "admin")
public class Auditoria {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String entidade;

    @Column(name = "entidade_id")
    private UUID entidadeId;

    @Column(nullable = false, length = 20)
    private String acao;

    @Column(name = "usuario_id")
    private UUID usuarioId; // FK para auth.users

    @Column(name = "dados_anteriores", columnDefinition = "jsonb")
    private String dadosAnteriores;

    @Column(name = "dados_novos", columnDefinition = "jsonb")
    private String dadosNovos;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}
