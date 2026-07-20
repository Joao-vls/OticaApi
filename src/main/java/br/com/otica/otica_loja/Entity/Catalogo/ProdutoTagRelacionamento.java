package br.com.otica.otica_loja.Entity.Catalogo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "produtos_tags", schema = "loja")
public class ProdutoTagRelacionamento {

    // Getters e Setters
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

}
