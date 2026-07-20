package br.com.otica.otica_loja.Entity.Estoque;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "estoque_movimentacoes", schema = "loja")
public class EstoqueMovimentacao {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private ProdutoVariante variante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMovimentacao tipo;


    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "saldo_anterior", nullable = false)
    private Integer saldoAnterior;

    @Column(name = "saldo_atual", nullable = false)
    private Integer saldoAtual;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "usuario_id")
    private UUID usuarioId; // FK para auth.users

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();


}
