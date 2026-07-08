package br.com.otica.otica_loja.Entity.Comercial;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UsuarioCupomId implements Serializable {

    private UUID usuarioId;
    private UUID cupomId;

    // Construtores
    public UsuarioCupomId() {}

    public UsuarioCupomId(UUID usuarioId, UUID cupomId) {
        this.usuarioId = usuarioId;
        this.cupomId = cupomId;
    }

    // Getters e Setters
    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getCupomId() {
        return cupomId;
    }

    public void setCupomId(UUID cupomId) {
        this.cupomId = cupomId;
    }

    // equals e hashCode usando Objects
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioCupomId)) return false;
        UsuarioCupomId that = (UsuarioCupomId) o;
        return Objects.equals(usuarioId, that.usuarioId) &&
                Objects.equals(cupomId, that.cupomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, cupomId);
    }
}
