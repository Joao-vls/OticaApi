package br.com.otica.otica_loja.Entity.Comercial;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
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
    private String tipo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "valor_minimo_pedido", precision = 12, scale = 2)
    private BigDecimal valorMinimoPedido;

    @Column(name = "limite_itens_por_pedido")
    private Integer limiteItensPorPedido;

    @Column(name = "quantidade_total")
    private Integer quantidadeTotal;

    @Column(name = "quantidade_utilizada", nullable = false)
    private Integer quantidadeUtilizada = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cupom_usuarios", schema = "loja", joinColumns = @JoinColumn(name = "cupom_id"))
    @Column(name = "usuario_id")
    private Set<UUID> usuariosIdsEspecificos = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cupom_produtos", schema = "loja", joinColumns = @JoinColumn(name = "cupom_id"))
    @Column(name = "produto_id")
    private Set<UUID> produtosIdsEspecificos = new HashSet<>();

    // 👇 NOVO CAMPO ADICIONADO: Categorias Específicas
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cupom_categorias", schema = "loja", joinColumns = @JoinColumn(name = "cupom_id"))
    @Column(name = "categoria_id")
    private Set<UUID> categoriasIdsEspecificas = new HashSet<>();
    // ------------------------- 👆

    @Column(name = "uso_unico", nullable = false)
    private Boolean usoUnico = false;

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