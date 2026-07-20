package br.com.otica.otica_loja.Entity.Catalogo;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Embeddable
public class ProdutoTagId implements Serializable {

    // Getters e Setters
    private UUID produtoId;
    private UUID tagId;

    // Construtores
    public ProdutoTagId() {}

    public ProdutoTagId(UUID produtoId, UUID tagId) {
        this.produtoId = produtoId;
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
