package br.com.otica.otica_loja.Repository.Pedidos;


import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, UUID> {

    // --- Projeção e Query Corrigida para o Grid do Supabase (PostgreSQL) ---

    interface AtividadeVendaProjection {
        LocalDate getData();
        Long getQuantidade();
        String getNomeProduto();
    }

    @Query(value = """
        SELECT\s
            CAST(pag.criado_em AS DATE) as data,
            SUM(i.quantidade) as quantidade,
            MAX(i.nome_produto) as nomeProduto
        FROM loja.pedido_itens i
        JOIN loja.pedidos p ON i.pedido_id = p.id
        JOIN loja.pagamentos pag ON pag.pedido_id = p.id
        WHERE EXTRACT(YEAR FROM pag.criado_em) = :ano
          AND pag.status = 'APROVADO'
        GROUP BY CAST(pag.criado_em AS DATE)
        ORDER BY data
       \s""", nativeQuery = true)
    List<AtividadeVendaProjection> obterAtividadeVendasPorAno(@Param("ano") int ano);

    // --- Seus métodos existentes de busca ---

    // Buscar itens de um pedido específico
    List<PedidoItem> findByPedido(Pedido pedido);

    // Buscar itens de um produto específico
    List<PedidoItem> findByProduto(Produto produto);

    // Buscar itens de uma variante específica
    List<PedidoItem> findByVariante(ProdutoVariante variante);

    // Buscar itens por SKU
    List<PedidoItem> findBySku(String sku);

    // Buscar itens com quantidade maior que um valor
    List<PedidoItem> findByQuantidadeGreaterThan(Integer quantidade);

    // Buscar itens com subtotal acima de um valor
    List<PedidoItem> findBySubtotalGreaterThan(BigDecimal subtotal);

    // Buscar itens ordenados por nome do produto
    List<PedidoItem> findAllByOrderByNomeProdutoAsc();
}