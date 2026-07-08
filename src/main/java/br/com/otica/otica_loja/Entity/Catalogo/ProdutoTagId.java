package br.com.otica.otica_loja.Entity.Catalogo;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProdutoTagId implements Serializable {

    private UUID produtoId;
    private UUID tagId;

    // Construtores
    public ProdutoTagId() {}

    public ProdutoTagId(UUID produtoId, UUID tagId) {
        this.produtoId = produtoId;
        this.tagId = tagId;
    }

    // Getters e Setters
    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public UUID getTagId() {
        return tagId;
    }

    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }

    // equals e hashCode usando Objects para evitar NullPointerException
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProdutoTagId)) return false;
        ProdutoTagId that = (ProdutoTagId) o;
        return Objects.equals(produtoId, that.produtoId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produtoId, tagId);
    }
}
