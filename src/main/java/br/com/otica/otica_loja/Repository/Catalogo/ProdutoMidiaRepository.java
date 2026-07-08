package br.com.otica.otica_loja.Repository.Catalogo;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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
}
