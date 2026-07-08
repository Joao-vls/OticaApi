package br.com.otica.otica_loja.Entity.Catalogo;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "produtos_tags", schema = "loja")
public class ProdutoTagRelacionamento {

    @EmbeddedId
    private ProdutoTagId id = new ProdutoTagId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produtoId")
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id", nullable = false)
    private ProdutoTag tag;

    // Getters e Setters
    public ProdutoTagId getId() {
        return id;
    }

    public void setId(ProdutoTagId id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public ProdutoTag getTag() {
        return tag;
    }

    public void setTag(ProdutoTag tag) {
        this.tag = tag;
    }
}
