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

    @Query("SELECT p FROM Produto p " +
            "LEFT JOIN FETCH p.variantes " +
            "LEFT JOIN FETCH p.midias " +
            "WHERE p.slug = :slug AND p.ativo = true")
    Optional<Produto> findBySlug(@Param("slug") String slug);

    // 🔥 BUSCA PARCIAL COM SUPORTE A UNACCENT E LOWERCASE (NATIVE QUERY POSTGRESQL)
    @Query(value = "SELECT * FROM loja.produtos p " +
            "WHERE unaccent(LOWER(p.nome)) LIKE unaccent(LOWER(CONCAT('%', :termo, '%'))) " +
            "AND p.ativo = true " +
            "AND p.deletado_em IS NULL",
            nativeQuery = true)
    List<Produto> findByNomeContainingIgnoreCase(@Param("termo") String termo);

    List<Produto> findByCategoriaId(UUID categoriaId);
    List<Produto> findByMarcaId(UUID marcaId);

    // 🔥 VERIFICA SE EXISTE ALGUM PRODUTO VINCULADO À MARCA
    boolean existsByMarcaId(UUID marcaId);

    List<Produto> findByAtivoTrue();
    List<Produto> findByAtivoFalse();
    List<Produto> findByDestaqueTrue();
    List<Produto> findByDeletadoEmIsNull();
    List<Produto> findByDeletadoEmIsNotNull();

    long countByAtivoTrue();
    long countByAtivoFalse();
}