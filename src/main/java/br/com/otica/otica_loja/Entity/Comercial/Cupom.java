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

    // LIMITE DE USOS GERAL
    @Column(name = "quantidade_total")
    private Integer quantidadeTotal;

    @Column(name = "quantidade_utilizada", nullable = false)
    private Integer quantidadeUtilizada = 0;

    // NOVOS CAMPOS ADICIONADOS 👇
    @Column(name = "usuario_id_especifico")
    private UUID usuarioIdEspecifico; // Se preenchido, só este usuário pode usar

    @Column(name = "produto_id_especifico")
    private UUID produtoIdEspecifico; // Se preenchido, exige que o produto esteja no pedido

    @Column(name = "uso_unico", nullable = false)
    private Boolean usoUnico = false; // Se true, o cupom é inativado após 1 uso (independente da quantidade total)
    // ------------------------- 👆

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