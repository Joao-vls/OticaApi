package br.com.otica.otica_loja.Entity.CMS;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "social_showcase", schema = "loja")
public class SocialShowcase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private Produto produto; // FK para loja.produtos (pode ser null)

    @Column(name = "marca_nome", length = 150)
    private String marcaNome;

    @Column(name = "modelo_nome", length = 150)
    private String modeloNome;

    @Column(nullable = false, length = 150)
    private String username;

    @Column(name = "thumbnail_path", nullable = false, columnDefinition = "TEXT")
    private String thumbnailPath;

    @Column(name = "video_path", nullable = false, columnDefinition = "TEXT")
    private String videoPath;

    @Column(nullable = false)
    private Long visualizacoes = 0L;

    @Column(nullable = false)
    private Long curtidas = 0L;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getMarcaNome() {
        return marcaNome;
    }

    public void setMarcaNome(String marcaNome) {
        this.marcaNome = marcaNome;
    }

    public String getModeloNome() {
        return modeloNome;
    }

    public void setModeloNome(String modeloNome) {
        this.modeloNome = modeloNome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Long getVisualizacoes() {
        return visualizacoes;
    }

    public void setVisualizacoes(Long visualizacoes) {
        this.visualizacoes = visualizacoes;
    }

    public Long getCurtidas() {
        return curtidas;
    }

    public void setCurtidas(Long curtidas) {
        this.curtidas = curtidas;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}