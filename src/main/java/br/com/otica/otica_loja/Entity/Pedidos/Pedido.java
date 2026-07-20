package br.com.otica.otica_loja.Entity.Pedidos;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.enums.StatusPedido;
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
@Table(name = "pedidos", schema = "loja")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // 👈 Evita que o Jackson quebre ao tentar serializar proxies LAZY vazios
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, updatable = false, insertable = false)
    private Long numero;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "endereco_id")
    private UUID enderecoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cupom_id")
    @JsonIgnoreProperties({
            "pedidos", // 👈 Evita recursão infinita se o Cupom mapear de volta os pedidos que o usaram
            "criadoEm", "atualizadoEm", "limiteUso", "quantidadeMaxUso", "ativo", // 👈 Remove metadados administrativos do cupom na resposta do pedido
            "hibernateLazyInitializer", "handler"
    })
    private Cupom cupom;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal frete = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();
}