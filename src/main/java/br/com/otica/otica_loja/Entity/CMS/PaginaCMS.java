package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "paginas_cms", schema = "loja")
public class PaginaCMS {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String resumo;

    @Column(name = "conteudo", nullable = false, columnDefinition = "JSONB")
    private String conteudo = "{}";

    @Column(name = "seo_title", length = 255)
    private String seoTitle;

    @Column(name = "seo_description", columnDefinition = "TEXT")
    private String seoDescription;

    @Column(nullable = false)
    private Boolean publicado = false;

    @Column(name = "publicado_em")
    private OffsetDateTime publicadoEm;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

}
