package br.com.otica.otica_loja.Entity.CMS;



import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "vitrine_produtos", schema = "loja")
public class VitrineProduto {

    @EmbeddedId
    private VitrineProdutoId id = new VitrineProdutoId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vitrineId")
    @JoinColumn(name = "vitrine_id", nullable = false)
    private Vitrine vitrine;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produtoId")
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer ordem = 0;

    // Getters e Setters
    public VitrineProdutoId getId() {
        return id;
    }

    public void setId(VitrineProdutoId id) {
        this.id = id;
    }

    public Vitrine getVitrine() {
        return vitrine;
    }

    public void setVitrine(Vitrine vitrine) {
        this.vitrine = vitrine;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}
