package br.com.otica.otica_loja.Entity.CMS;



import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "vitrine_produtos", schema = "loja")
public class VitrineProduto {

    // Getters e Setters
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

}
