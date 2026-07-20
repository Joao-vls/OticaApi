package br.com.otica.otica_loja.Entity.CRM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "leads", schema = "loja")
public class Lead {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 255)
    private String nome;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String telefone;

    @Column(length = 100)
    private String origem;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(nullable = false)
    private Boolean convertido = false;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}
