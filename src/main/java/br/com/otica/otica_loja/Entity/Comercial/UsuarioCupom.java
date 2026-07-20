package br.com.otica.otica_loja.Entity.Comercial;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
@Entity
@Table(name = "usuario_cupons", schema = "loja")
public class UsuarioCupom {

    // Getters e Setters
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

}
