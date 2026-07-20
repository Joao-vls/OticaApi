package br.com.otica.otica_loja.Repository.Catalogo;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    // Buscar produto pelo slug
    @Query("SELECT p FROM Produto p " +
            "LEFT JOIN FETCH p.variantes " +
            "LEFT JOIN FETCH p.midias " +
            "WHERE p.slug = :slug AND p.ativo = true")
    Optional<Produto> findBySlug(@Param("slug") String slug);

    // Buscar produtos por nome (contendo parte do texto)
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Buscar produtos por categoria
    List<Produto> findByCategoriaId(UUID categoriaId);

    // Buscar produtos por marca
    List<Produto> findByMarcaId(UUID marcaId);

    // Buscar produtos ativos
    List<Produto> findByAtivoTrue();

    // Buscar produtos inativos
    List<Produto> findByAtivoFalse();

    // Buscar produtos em destaque
    List<Produto> findByDestaqueTrue();

    // Buscar produtos não deletados (soft delete)
    List<Produto> findByDeletadoEmIsNull();

    // Buscar produtos deletados (soft delete)
    List<Produto> findByDeletadoEmIsNotNull();

    long countByAtivoTrue();
    long countByAtivoFalse();
}
