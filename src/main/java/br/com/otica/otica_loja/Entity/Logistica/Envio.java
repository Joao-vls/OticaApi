package br.com.otica.otica_loja.Entity.Logistica;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "envios", schema = "loja")
public class Envio {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportadora_id")
    private Transportadora transportadora;

    @Column(name = "codigo_rastreio")
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

}