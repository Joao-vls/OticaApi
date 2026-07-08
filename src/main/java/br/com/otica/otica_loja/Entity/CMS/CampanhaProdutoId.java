package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CampanhaProdutoId implements Serializable {

    private UUID campanhaId;
    private UUID produtoId;

    // Getters e Setters
    public UUID getCampanhaId() {
        return campanhaId;
    }

    public void setCampanhaId(UUID campanhaId) {
        this.campanhaId = campanhaId;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CampanhaProdutoId)) return false;
        CampanhaProdutoId that = (CampanhaProdutoId) o;
        return Objects.equals(campanhaId, that.campanhaId) &&
                Objects.equals(produtoId, that.produtoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(campanhaId, produtoId);
    }
}