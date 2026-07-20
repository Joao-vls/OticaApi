package br.com.otica.otica_loja.Entity.Pedidos;

import br.com.otica.otica_loja.enums.StatusTrocaDevolucao;
import br.com.otica.otica_loja.enums.TipoTrocaDevolucao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "trocas_devolucoes", schema = "loja")
public class TrocaDevolucao {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId; // FK para auth.users


    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoTrocaDevolucao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusTrocaDevolucao status = StatusTrocaDevolucao.SOLICITADO;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

}