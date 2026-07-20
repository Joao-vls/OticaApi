package br.com.otica.otica_loja.Entity.Catalogo;

import br.com.otica.otica_loja.enums.TipoMidia;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "produto_midias", schema = "loja")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Removido o bloqueio global da variante
public class ProdutoMidia {

    @Id
    @GeneratedValue // Otimizado para UUID nativo
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", foreignKey = @ForeignKey(name = "produto_midias_produto_id_fkey"))
    @JsonIgnore // 👈 Adicione isso. A mídia não precisa devolver o produto inteiro dentro dela no JSON.
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", foreignKey = @ForeignKey(name = "produto_midias_variante_id_fkey"))
    @JsonIgnoreProperties({
            "produto", "midias", // 👈 Adicionado "midias" aqui para evitar que a variante puxe a lista de mídias de volta!
            "nome", "codigoBarras", "colorName", "colorHex", "colorImagePath",
            "pesoGramas", "stock", "estoqueMinimo", "priceOverride", "ativo",
            "criadoEm", "atualizadoEm", "deletadoEm", "hibernateLazyInitializer", "handler"
    })
    private ProdutoVariante variante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMidia tipo;

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
}