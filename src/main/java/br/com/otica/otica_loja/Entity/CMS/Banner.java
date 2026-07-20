package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "banners", schema = "loja")
public class Banner {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String subtitulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "imagem_desktop_path", nullable = false, columnDefinition = "TEXT")
    private String imagemDesktopPath;

    @Column(name = "imagem_mobile_path", columnDefinition = "TEXT")
    private String imagemMobilePath;

    @Column(name = "link_url", columnDefinition = "TEXT")
    private String linkUrl;

    @Column(name = "botao_texto", length = 150)
    private String botaoTexto;

    @Column(length = 50)
    private String posicao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_inicio")
    private OffsetDateTime dataInicio;

    @Column(name = "data_fim")
    private OffsetDateTime dataFim;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

}