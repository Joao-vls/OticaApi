package br.com.otica.otica_loja.Repository.Pedidos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.PedidoItem;
import org.springframework.data.domain.Pageable; // ✅ IMPORT CORRETO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        SELECT 
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
        """, nativeQuery = true)
    List<AtividadeVendaProjection> obterAtividadeVendasPorAno(@Param("ano") int ano);

    interface UltimaVendaProjection {
        String getProduct();
        String getClient();
        String getLocation();
        LocalDateTime getDateTime();
    }

    @Query(value = """
        SELECT 
            i.nome_produto AS product,
            COALESCE(pf.nome, u.nome, 'Cliente N/A') AS client,
            COALESCE(e.cidade, 'N/A') AS location,
            pag.criado_em AS dateTime
        FROM loja.pedido_itens i
        JOIN loja.pedidos p ON i.pedido_id = p.id
        JOIN loja.pagamentos pag ON pag.pedido_id = p.id
        LEFT JOIN loja.usuarios u ON p.usuario_id = u.id
        LEFT JOIN app.perfis pf ON u.id = pf.id
        LEFT JOIN app.enderecos e ON p.endereco_id = e.id
        WHERE pag.status = 'APROVADO'
        ORDER BY pag.criado_em DESC
        """, nativeQuery = true)
    List<UltimaVendaProjection> obterUltimasVendas(Pageable pageable);

    // --- Seus métodos existentes de busca ---

    // Buscar itens de um pedido específico
    List<PedidoItem> findByPedido(Pedido pedido);

    // Buscar itens de um produto específico
    List<PedidoItem> findByProduto(Produto produto);

    boolean existsByProduto(Produto produto);

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