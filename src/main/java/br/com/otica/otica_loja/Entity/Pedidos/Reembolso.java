package br.com.otica.otica_loja.Entity.Pedidos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "reembolsos", schema = "loja")
public class Reembolso {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pagamento_id", nullable = false)
    private Pagamento pagamento;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}