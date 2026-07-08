package br.com.otica.otica_loja.Entity.Catalogo;

import jakarta.persistence.*;


import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "produto_midias", schema = "loja")
public class ProdutoMidia {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private UUID id;

    // Relacionamento com Produto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", foreignKey = @ForeignKey(name = "produto_midias_produto_id_fkey"))
    private Produto produto;

    // Relacionamento com ProdutoVariantes
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", foreignKey = @ForeignKey(name = "produto_midias_variante_id_fkey"))
    private ProdutoVariante variante;

    @Column(nullable = false, length = 20)
    private String tipo; // image, video, 3d

    @Column(nullable = false, columnDefinition = "TEXT")
    private String path;

    @Column(name = "thumbnail_path", columnDefinition = "TEXT")
    private String thumbnailPath;

    @Column(name = "poster_path", columnDefinition = "TEXT")
    private String posterPath;

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

    public ProdutoVariante getVariante() {
        return variante;
    }

    public void setVariante(ProdutoVariante variante) {
        this.variante = variante;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
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
