package br.com.otica.otica_loja.Entity.Avaliacao;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Embeddable
public class FavoritoId implements Serializable {

    // Getters e Setters
    private UUID usuarioId;
    private UUID produtoId;

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
