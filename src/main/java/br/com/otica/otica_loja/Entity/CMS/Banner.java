package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "banners", schema = "loja")
public class Banner {

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

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagemDesktopPath() {
        return imagemDesktopPath;
    }

    public void setImagemDesktopPath(String imagemDesktopPath) {
        this.imagemDesktopPath = imagemDesktopPath;
    }

    public String getImagemMobilePath() {
        return imagemMobilePath;
    }

    public void setImagemMobilePath(String imagemMobilePath) {
        this.imagemMobilePath = imagemMobilePath;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getBotaoTexto() {
        return botaoTexto;
    }

    public void setBotaoTexto(String botaoTexto) {
        this.botaoTexto = botaoTexto;
    }

    public String getPosicao() {
        return posicao;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public OffsetDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(OffsetDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public OffsetDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(OffsetDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(OffsetDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}