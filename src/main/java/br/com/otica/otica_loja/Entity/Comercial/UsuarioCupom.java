package br.com.otica.otica_loja.Entity.Comercial;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "usuario_cupons", schema = "loja")
public class UsuarioCupom {

    @EmbeddedId
    private UsuarioCupomId id = new UsuarioCupomId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // ✅ relacionamento direto com Usuario

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cupomId")
    @JoinColumn(name = "cupom_id", nullable = false)
    private Cupom cupom;

    @Column(name = "utilizado_em", nullable = false)
    private OffsetDateTime utilizadoEm = OffsetDateTime.now();

    // Getters e Setters
    public UsuarioCupomId getId() {
        return id;
    }

    public void setId(UsuarioCupomId id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Cupom getCupom() {
        return cupom;
    }

    public void setCupom(Cupom cupom) {
        this.cupom = cupom;
    }

    public OffsetDateTime getUtilizadoEm() {
        return utilizadoEm;
    }

    public void setUtilizadoEm(OffsetDateTime utilizadoEm) {
        this.utilizadoEm = utilizadoEm;
    }
}
