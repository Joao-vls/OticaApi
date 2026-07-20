package br.com.otica.otica_loja.Entity.CMS;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "campanha_produtos", schema = "loja")
public class CampanhaProduto {

    // Getters e Setters
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

}