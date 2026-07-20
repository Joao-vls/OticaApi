package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "menu_banners", schema = "loja")
public class MenuBanner {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String contexto;
    // sol, grau, institucional, geral

    @Column()
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String subtitulo;

    @Column(name = "imagem_path", nullable = false, columnDefinition = "TEXT")
    private String imagemPath;

    @Column(name = "link_url", columnDefinition = "TEXT")
    private String linkUrl;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}
