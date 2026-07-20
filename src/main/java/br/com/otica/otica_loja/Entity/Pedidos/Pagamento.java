package br.com.otica.otica_loja.Entity.Pedidos;

import br.com.otica.otica_loja.enums.MetodoPagamento;
import br.com.otica.otica_loja.enums.StatusPagamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "pagamentos", schema = "loja")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore // 👈 Corta o loop de volta para o pedido
    private Pedido pedido;

    @Column(length = 50)
    private String gateway;

    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MetodoPagamento metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPagamento status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(columnDefinition = "jsonb")
    @JsonIgnore // 👈 ESSENCIAL: Ignora o payload bruto do gateway para o JSON não explodir de tamanho
    private String payload;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();
}