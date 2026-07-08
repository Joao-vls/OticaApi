package br.com.otica.otica_loja.Entity.CMS;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "campanha_produtos", schema = "loja")
public class CampanhaProduto {

    @EmbeddedId
    private CampanhaProdutoId id = new CampanhaProdutoId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("campanhaId")
    @JoinColumn(name = "campanha_id", nullable = false)
    private Campanha campanha;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produtoId")
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Getters e Setters
    public CampanhaProdutoId getId() {
        return id;
    }

    public void setId(CampanhaProdutoId id) {
        this.id = id;
    }

    public Campanha getCampanha() {
        return campanha;
    }

    public void setCampanha(Campanha campanha) {
        this.campanha = campanha;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}