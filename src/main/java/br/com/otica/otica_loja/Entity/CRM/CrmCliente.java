package br.com.otica.otica_loja.Entity.CRM;

import br.com.otica.otica_loja.enums.NivelCliente;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "crm_clientes", schema = "loja")
public class CrmCliente {

    @Id
    @Column(name = "usuario_id")
    private UUID usuarioId; // FK para auth.users

    @Column(nullable = false)
    private Integer score = 0;

    @Column(name = "total_pedidos", nullable = false)
    private Integer totalPedidos = 0;

    @Column(name = "valor_gasto", precision = 14, scale = 2, nullable = false)
    private BigDecimal valorGasto = BigDecimal.ZERO;

    @Column(name = "ultimo_pedido_em")
    private OffsetDateTime ultimoPedidoEm;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NivelCliente nivel = NivelCliente.BRONZE;


    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    // Getters e Setters
    public NivelCliente getNivel() {
        return nivel;
    }

    public void setNivel(NivelCliente nivel) {
        this.nivel = nivel;
    }
    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotalPedidos() {
        return totalPedidos;
    }

    public void setTotalPedidos(Integer totalPedidos) {
        this.totalPedidos = totalPedidos;
    }

    public BigDecimal getValorGasto() {
        return valorGasto;
    }

    public void setValorGasto(BigDecimal valorGasto) {
        this.valorGasto = valorGasto;
    }

    public OffsetDateTime getUltimoPedidoEm() {
        return ultimoPedidoEm;
    }

    public void setUltimoPedidoEm(OffsetDateTime ultimoPedidoEm) {
        this.ultimoPedidoEm = ultimoPedidoEm;
    }



    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(OffsetDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}