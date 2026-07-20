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
public class VitrineProdutoId implements Serializable {

    // Getters e Setters
    private UUID vitrineId;
    private UUID produtoId;

    // Construtor padrão (necessário para JPA)
    public VitrineProdutoId() {}

    // Construtor com parâmetros
    public VitrineProdutoId(UUID vitrineId, UUID produtoId) {
        this.vitrineId = vitrineId;
        this.produtoId = produtoId;
    }

    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VitrineProdutoId that)) return false;
        return Objects.equals(vitrineId, that.vitrineId) &&
                Objects.equals(produtoId, that.produtoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vitrineId, produtoId);
    }
}
