package br.com.otica.otica_loja.Repository.Catalogo;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
