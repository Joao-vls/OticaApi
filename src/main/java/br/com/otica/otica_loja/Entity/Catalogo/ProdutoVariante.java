package br.com.otica.otica_loja.Entity.Catalogo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "produto_variantes", schema = "loja")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProdutoVariante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Relacionamento com Produto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false,
            foreignKey = @ForeignKey(name = "produto_variantes_produto_id_fkey"))
    @JsonIgnore// Evita serializar recursivamente ao iniciar pela variante
    private Produto produto;

    // 🔥 Adicionado: Relacionamento reverso com as mídias da variante para pegar na ordem certa
    @OneToMany(mappedBy = "variante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("ordem ASC") // Garante que venha na sequência (1, 2, 3...) cadastrada no banco
    @JsonIgnore
    private Set<ProdutoMidia> midias = new LinkedHashSet<>();

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
}