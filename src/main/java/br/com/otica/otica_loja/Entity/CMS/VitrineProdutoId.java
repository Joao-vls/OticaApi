package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class VitrineProdutoId implements Serializable {

    private UUID vitrineId;
    private UUID produtoId;

    // Construtor padrão (necessário para JPA)
    public VitrineProdutoId() {}

    // Construtor com parâmetros
    public VitrineProdutoId(UUID vitrineId, UUID produtoId) {
        this.vitrineId = vitrineId;
        this.produtoId = produtoId;
    }

    // Getters e Setters
    public UUID getVitrineId() {
        return vitrineId;
    }

    public void setVitrineId(UUID vitrineId) {
        this.vitrineId = vitrineId;
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
        if (!(o instanceof VitrineProdutoId)) return false;
        VitrineProdutoId that = (VitrineProdutoId) o;
        return Objects.equals(vitrineId, that.vitrineId) &&
                Objects.equals(produtoId, that.produtoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vitrineId, produtoId);
    }
}
