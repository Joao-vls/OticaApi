package br.com.otica.otica_loja.Entity.Carrinho;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "carrinho_itens", schema = "loja",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_carrinho_variante", columnNames = {"carrinho_id", "variante_id"})
        })
public class CarrinhoItem {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrinho_id", nullable = false)
    private Carrinho carrinho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private ProdutoVariante variante;

    @Column(nullable = false)
    private Integer quantidade = 1;

    @Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}
