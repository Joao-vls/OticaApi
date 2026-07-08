package br.com.otica.otica_loja.Entity.Catalogo;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "produtos", schema = "loja")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "marca_id", nullable = false)
    private UUID marcaId;

    @Column(name = "categoria_id", nullable = false)
    private UUID categoriaId;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(name = "categoria_oculos", nullable = false, length = 20)
    private String categoriaOculos;

    @Column(name = "specs", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String specs = "{}";

    @Column(nullable = false)
    private Boolean destaque = false;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "search_vector", columnDefinition = "tsvector", insertable = false, updatable = false)
    private String searchVector;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    @Column(name = "deletado_em")
    private OffsetDateTime deletadoEm;

    // Relacionamento com variantes
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProdutoVariante> variantes;

    // Relacionamento com mídias
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProdutoMidia> midias;

    // Relacionamento com tags
    @ManyToMany
    @JoinTable(
            name = "produtos_tags",
            schema = "loja",
            joinColumns = @JoinColumn(name = "produto_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<ProdutoTag> tags;

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMarcaId() {
        return marcaId;
    }

    public void setMarcaId(UUID marcaId) {
        this.marcaId = marcaId;
    }

    public UUID getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(UUID categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getCategoriaOculos() {
        return categoriaOculos;
    }

    public void setCategoriaOculos(String categoriaOculos) {
        this.categoriaOculos = categoriaOculos;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public Boolean getDestaque() {
        return destaque;
    }

    public void setDestaque(Boolean destaque) {
        this.destaque = destaque;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getSearchVector() {
        return searchVector;
    }

    public void setSearchVector(String searchVector) {
        this.searchVector = searchVector;
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

    public OffsetDateTime getDeletadoEm() {
        return deletadoEm;
    }

    public void setDeletadoEm(OffsetDateTime deletadoEm) {
        this.deletadoEm = deletadoEm;
    }

    public List<ProdutoVariante> getVariantes() {
        return variantes;
    }

    public void setVariantes(List<ProdutoVariante> variantes) {
        this.variantes = variantes;
    }

    public List<ProdutoMidia> getMidias() {
        return midias;
    }

    public void setMidias(List<ProdutoMidia> midias) {
        this.midias = midias;
    }

    public List<ProdutoTag> getTags() {
        return tags;
    }

    public void setTags(List<ProdutoTag> tags) {
        this.tags = tags;
    }
}
