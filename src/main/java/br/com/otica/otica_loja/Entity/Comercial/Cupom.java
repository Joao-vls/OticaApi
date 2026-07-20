package br.com.otica.otica_loja.Entity.Comercial;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "cupons", schema = "loja")
public class Cupom {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 20)
    private String tipo; // percentual, fixo, frete

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "valor_minimo_pedido", precision = 12, scale = 2)
    private BigDecimal valorMinimoPedido;

    @Column(name = "quantidade_total")
    private Integer quantidadeTotal;

    @Column(name = "quantidade_utilizada", nullable = false)
    private Integer quantidadeUtilizada = 0;

    @Column(name = "data_inicio")
    private OffsetDateTime dataInicio;

    @Column(name = "data_fim")
    private OffsetDateTime dataFim;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

}
