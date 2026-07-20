package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Embeddable
public class CampanhaProdutoId implements Serializable {

    // Getters e Setters
    private UUID campanhaId;
    private UUID produtoId;

    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CampanhaProdutoId that)) return false;
        return Objects.equals(campanhaId, that.campanhaId) &&
                Objects.equals(produtoId, that.produtoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(campanhaId, produtoId);
    }
}