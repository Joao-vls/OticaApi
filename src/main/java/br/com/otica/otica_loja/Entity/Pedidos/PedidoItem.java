package br.com.otica.otica_loja.Entity.Pedidos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "pedido_itens", schema = "loja")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore // 👈 O item não precisa devolver o pedido inteiro dentro dele
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    @JsonIgnoreProperties({"midias", "variantes", "descricao", "criadoEm", "atualizadoEm", "hibernateLazyInitializer", "handler"}) // 👈 Evita carregar o catálogo inteiro aqui
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id")
    @JsonIgnoreProperties({"produto", "midias", "hibernateLazyInitializer", "handler"}) // 👈 Limpa o nó da variante
    private ProdutoVariante variante;

    @Column(name = "nome_produto", nullable = false)
    private String nomeProduto;

    @Column(length = 100)
    private String sku;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}