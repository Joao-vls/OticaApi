package br.com.otica.otica_loja.Repository.Catalogo;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.enums.TipoMidia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProdutoMidiaRepository extends JpaRepository<ProdutoMidia, UUID> {

    // Buscar todas as mídias de um produto
    List<ProdutoMidia> findByProduto(Produto produto);

    // Buscar todas as mídias de uma variante
    List<ProdutoMidia> findByVariante(ProdutoVariante variante);

    // Buscar mídias de um produto por tipo (image, video, 3d)
    List<ProdutoMidia> findByProdutoAndTipo(Produto produto, String tipo);

    // Buscar mídias de uma variante por tipo
    List<ProdutoMidia> findByVarianteAndTipo(ProdutoVariante variante, String tipo);

    // Buscar mídias de um produto ordenadas pela ordem
    List<ProdutoMidia> findByProdutoOrderByOrdemAsc(Produto produto);
    // 1º Tenta achar a 1ª imagem associada especificamente à VARIANTE
    @Query("SELECT m.path FROM ProdutoMidia m WHERE m.variante.id = :varianteId AND m.tipo = :tipo ORDER BY m.ordem ASC LIMIT 1")
    Optional<String> findFirstImagemByVarianteId(@Param("varianteId") UUID varianteId, @Param("tipo") TipoMidia tipo);

    // 2º (Fallback) Se a variante não tiver imagem própria, pega a 1ª imagem do PRODUTO PAI
    @Query("SELECT m.path FROM ProdutoMidia m WHERE m.produto.id = :produtoId AND m.tipo = :tipo AND m.variante IS NULL ORDER BY m.ordem ASC LIMIT 1")
    Optional<String> findFirstImagemByProdutoId(@Param("produtoId") UUID produtoId, @Param("tipo") TipoMidia tipo);
}
