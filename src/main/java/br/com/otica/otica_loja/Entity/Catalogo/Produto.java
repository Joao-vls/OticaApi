package br.com.otica.otica_loja.Entity.Catalogo;

import br.com.otica.otica_loja.enums.GrauOculos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "produtos", schema = "loja")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Relacionamento com Marca para extrair o nome
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id", insertable = false, updatable = false)
    private Marca marca;

    @Column(name = "marca_id", nullable = false)
    private UUID marcaId;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @ElementCollection(targetClass = GrauOculos.class)
    @CollectionTable(
            name = "produtos_graus_disponiveis",
            schema = "loja",
            joinColumns = @JoinColumn(name = "produto_id")
    )
    @Column(name = "grau_oculos", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<GrauOculos> grausDisponiveis = new LinkedHashSet<>();

    @Column(name = "specs", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String specs = "{}";

    @Column(name = "permite_lente_grau", nullable = false)
    private Boolean permiteLenteGrau = false;

    @Column(name = "permite_receita", nullable = false)
    private Boolean permiteReceita = false;

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

    @ManyToMany
    @JoinTable(
            name = "produtos_categorias",
            schema = "loja",
            joinColumns = @JoinColumn(name = "produto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new LinkedHashSet<>();

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("nome DESC")
    @JsonIgnoreProperties("produto")
    private Set<ProdutoVariante> variantes = new LinkedHashSet<>();

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