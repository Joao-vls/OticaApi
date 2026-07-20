package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "home_sections", schema = "loja")
public class HomeSection {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String identificador;

    @Column()
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String subtitulo;

    @Column(nullable = false, length = 50)
    private String tipo; // hero, banner, vitrine, social, editorial, newsletter, brands, custom

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(name = "configuracao", columnDefinition = "jsonb", nullable = false)
    private String configuracao = "{}";

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

}
