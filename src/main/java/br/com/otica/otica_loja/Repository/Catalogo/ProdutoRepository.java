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
    @Query(value = "SELECT kv.key AS chave, kv.value AS valor " +
            "FROM loja.produtos p, " +
            "jsonb_each_text(p.specs::jsonb) AS kv " +
            "WHERE p.ativo = true AND p.deletado_em IS NULL",
            nativeQuery = true)
    List<SpecKeyValueProjection> buscarTodasSpecsAtivas();

    interface SpecKeyValueProjection {
        String getChave();
        String getValor();
    }
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

    // 🔥 BUSCA GLOBAL COMPLETA (Ignora Acentos e Maiúsculas/Minúsculas usando ILIKE)
    @Query(value = "SELECT DISTINCT p.* FROM loja.produtos p " +
            "LEFT JOIN loja.marcas m ON p.marca_id = m.id " +
            "LEFT JOIN loja.produto_variantes v ON v.produto_id = p.id " +
            "LEFT JOIN loja.produto_categoria pc ON pc.produto_id = p.id " +
            "LEFT JOIN loja.categorias c ON c.id = pc.categoria_id " +
            "WHERE p.ativo = true AND p.deletado_em IS NULL " +
            "AND (" +
            "   unaccent(p.nome) ILIKE unaccent(CONCAT('%', :termo, '%')) " +
            "   OR unaccent(p.descricao) ILIKE unaccent(CONCAT('%', :termo, '%')) " +
            "   OR unaccent(p.specs::text) ILIKE unaccent(CONCAT('%', :termo, '%')) " + // JSONB Imune a case e acento
            "   OR unaccent(m.nome) ILIKE unaccent(CONCAT('%', :termo, '%')) " +
            "   OR unaccent(c.nome) ILIKE unaccent(CONCAT('%', :termo, '%')) " +
            "   OR v.sku ILIKE CONCAT('%', :termo, '%') " +
            "   OR v.codigo_barras ILIKE CONCAT('%', :termo, '%') " +
            "   OR unaccent(v.color_name) ILIKE unaccent(CONCAT('%', :termo, '%')) " +
            ")",
            nativeQuery = true)
    List<Produto> pesquisaGlobalCompleta(@Param("termo") String termo);

    // 🔥 BUSCA EM N:N (Mapeia a propriedade 'id' dentro do Set<Categoria> 'categorias')
    List<Produto> findByCategorias_Id(UUID categoriaId);

    // 🔥 VERIFICA SE EXISTE PRODUTO VINCULADO À CATEGORIA
    boolean existsByCategorias_Id(UUID categoriaId);

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