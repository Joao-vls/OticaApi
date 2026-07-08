package br.com.otica.otica_loja.Entity.Catalogo;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "produto_variantes", schema = "loja")
public class ProdutoVariante {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private UUID id;

    // Relacionamento com Produto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false,
            foreignKey = @ForeignKey(name = "produto_variantes_produto_id_fkey"))
    private Produto produto;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "codigo_barras", length = 100)
    private String codigoBarras;

    @Column(name = "color_name", length = 150)
    private String colorName;

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @Column(name = "color_image_path", columnDefinition = "TEXT")
    private String colorImagePath;

    @Column(name = "peso_gramas", precision = 10, scale = 2)
    private BigDecimal pesoGramas;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo = 0;

    @Column(name = "price_override", precision = 12, scale = 2)
    private BigDecimal priceOverride;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    @Column(name = "deletado_em")
    private OffsetDateTime deletadoEm;

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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getColorImagePath() {
        return colorImagePath;
    }

    public void setColorImagePath(String colorImagePath) {
        this.colorImagePath = colorImagePath;
    }

    public BigDecimal getPesoGramas() {
        return pesoGramas;
    }

    public void setPesoGramas(BigDecimal pesoGramas) {
        this.pesoGramas = pesoGramas;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public BigDecimal getPriceOverride() {
        return priceOverride;
    }

    public void setPriceOverride(BigDecimal priceOverride) {
        this.priceOverride = priceOverride;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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
}
