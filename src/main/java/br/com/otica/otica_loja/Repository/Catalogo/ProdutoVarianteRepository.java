package br.com.otica.otica_loja.Repository.Catalogo;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProdutoVarianteRepository extends JpaRepository<ProdutoVariante, UUID> {

    // Buscar variante pelo SKU
    Optional<ProdutoVariante> findBySku(String sku);

    // Buscar variante pelo código de barras
    Optional<ProdutoVariante> findByCodigoBarras(String codigoBarras);

    // Buscar todas as variantes de um produto
    List<ProdutoVariante> findByProduto(Produto produto);

    // Buscar variantes ativas de um produto
    List<ProdutoVariante> findByProdutoAndAtivoTrue(Produto produto);

    // Buscar variantes inativas de um produto
    List<ProdutoVariante> findByProdutoAndAtivoFalse(Produto produto);

    // Buscar variantes não deletadas (soft delete)
    List<ProdutoVariante> findByDeletadoEmIsNull();

    // Buscar variantes deletadas (soft delete)
    List<ProdutoVariante> findByDeletadoEmIsNotNull();

    // Buscar variantes com estoque abaixo do mínimo
    List<ProdutoVariante> findByStockLessThanEqual(Integer estoqueMinimo);

    @Query("SELECT COUNT(v.id) FROM ProdutoVariante v WHERE v.ativo = true AND v.deletadoEm IS NULL")
    long contarVariantesAtivas();

    @Query("SELECT SUM(v.stock) FROM ProdutoVariante v WHERE v.ativo = true AND v.deletadoEm IS NULL")
    Long somarTotalItensEmEstoque();

    @Query("SELECT COUNT(v.id) FROM ProdutoVariante v WHERE v.ativo = true AND v.deletadoEm IS NULL AND v.stock = 0")
    long contarVariantesSemEstoque();

    @Query("SELECT COUNT(v.id) FROM ProdutoVariante v WHERE v.ativo = true AND v.deletadoEm IS NULL AND v.stock > 0 AND v.stock <= v.estoqueMinimo")
    long contarVariantesEstoqueBaixo();

    // Calcula Patrimônio: Usa o PriceOverride se existir, senão usa o preço base do Produto, e multiplica pelo Estoque.
    @Query("SELECT SUM(v.stock * COALESCE(v.priceOverride, p.preco)) " +
            "FROM ProdutoVariante v JOIN v.produto p " +
            "WHERE v.ativo = true AND v.deletadoEm IS NULL AND p.ativo = true AND p.deletadoEm IS NULL")
    java.math.BigDecimal calcularValorTotalEstoque();

    public interface AlertaEstoqueProjection {
        String getId();
        String getNomeProduto();
        String getSku();
        Integer getStock();
        Integer getEstoqueMinimo();
        String getColorName();
    }

    // Busca itens críticos para a tela do Gestor
    @Query("SELECT v.id as id, p.nome as nomeProduto, v.sku as sku, v.stock as stock, v.estoqueMinimo as estoqueMinimo, v.colorName as colorName " +
            "FROM ProdutoVariante v JOIN v.produto p " +
            "WHERE v.ativo = true AND v.deletadoEm IS NULL AND p.ativo = true " +
            "AND v.stock <= v.estoqueMinimo " +
            "ORDER BY v.stock ASC")
    List<AlertaEstoqueProjection> buscarItensAbaixoEstoqueMinimo(Pageable pageable);
}
