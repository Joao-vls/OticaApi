package br.com.otica.otica_loja.Repository.Pedidos;

import br.com.otica.otica_loja.Entity.Pedidos.PedidoItem;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, UUID> {

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
