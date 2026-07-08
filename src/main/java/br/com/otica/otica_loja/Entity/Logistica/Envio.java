package br.com.otica.otica_loja.Entity.Logistica;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "envios", schema = "loja")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportadora_id")
    private Transportadora transportadora;

    @Column(name = "codigo_rastreio", length = 255)
    private String codigoRastreio;

    @Column(name = "url_rastreio", columnDefinition = "TEXT")
    private String urlRastreio;

    @Column(name = "valor_frete", precision = 12, scale = 2)
    private BigDecimal valorFrete;

    @Column(name = "enviado_em")
    private OffsetDateTime enviadoEm;

    @Column(name = "entregue_em")
    private OffsetDateTime entregueEm;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Transportadora getTransportadora() {
        return transportadora;
    }

    public void setTransportadora(Transportadora transportadora) {
        this.transportadora = transportadora;
    }

    public String getCodigoRastreio() {
        return codigoRastreio;
    }

    public void setCodigoRastreio(String codigoRastreio) {
        this.codigoRastreio = codigoRastreio;
    }

    public String getUrlRastreio() {
        return urlRastreio;
    }

    public void setUrlRastreio(String urlRastreio) {
        this.urlRastreio = urlRastreio;
    }

    public BigDecimal getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(BigDecimal valorFrete) {
        this.valorFrete = valorFrete;
    }

    public OffsetDateTime getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(OffsetDateTime enviadoEm) {
        this.enviadoEm = enviadoEm;
    }

    public OffsetDateTime getEntregueEm() {
        return entregueEm;
    }

    public void setEntregueEm(OffsetDateTime entregueEm) {
        this.entregueEm = entregueEm;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}