package br.com.otica.otica_loja.Entity.Avaliacao;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class FavoritoId implements Serializable {

    private UUID usuarioId;
    private UUID produtoId;

    // Getters e Setters
    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    // equals e hashCode usando Objects para evitar NullPointerException
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoritoId)) return false;
        FavoritoId that = (FavoritoId) o;
        return Objects.equals(usuarioId, that.usuarioId) &&
                Objects.equals(produtoId, that.produtoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, produtoId);
    }
}
