package br.com.otica.otica_loja.Entity.Catalogo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet; // 🔥 Importado para preservar a ordenação original do banco
import java.util.Set; // 🔥 Trocado de List para Set
import java.util.UUID;

@Setter
@Getter
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
    private OffsetDateTime atualizadoEm = OffsetDateTime.now(); // Nota: Alinhado com o padrão de nomenclatura interna se necessário

    @Column(name = "deletado_em")
    private OffsetDateTime deletadoEm;

// Dentro de Produto.java


    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("nome DESC")
    @JsonIgnoreProperties("produto")
    private Set<ProdutoVariante> variantes = new LinkedHashSet<>();

    // Ordena primeiro as mídias por tipo (colocando IMAGE ou VIDEO agrupados) e pela coluna ordem de forma crescente (1, 2, 3...)
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("tipo DESC, ordem ASC")
    @JsonIgnoreProperties("produto")
    private Set<ProdutoMidia> midias = new LinkedHashSet<>();


    @ManyToMany
    @JoinTable(
            name = "produtos_tags",
            schema = "loja",
            joinColumns = @JoinColumn(name = "produto_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<ProdutoTag> tags = new LinkedHashSet<>();
}