package br.com.otica.otica_loja.Repository.Catalogo;

import br.com.otica.otica_loja.Entity.Catalogo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    // Buscar categoria pelo nome
    Optional<Categoria> findByNome(String nome);

    // Buscar categoria pelo slug
    Optional<Categoria> findBySlug(String slug);

    // Verificar se existe categoria com determinado nome
    boolean existsByNome(String nome);

    // Verificar se existe categoria com determinado slug
    boolean existsBySlug(String slug);

    // Buscar todas as categorias ativas
    List<Categoria> findByAtivoTrue();

    // Buscar todas as categorias inativas
    List<Categoria> findByAtivoFalse();
}
