package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.MenuCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuCategoriaRepository extends JpaRepository<MenuCategoria, UUID> {

    // Buscar categoria pelo slug
    Optional<MenuCategoria> findBySlug(String slug);

    // Verificar se já existe categoria com determinado slug
    boolean existsBySlug(String slug);

    // Buscar categorias por contexto (ex.: sol, grau, institucional, geral)
    List<MenuCategoria> findByContexto(String contexto);

    // Buscar categorias ativas
    List<MenuCategoria> findByAtivoTrue();

    // Buscar categorias inativas
    List<MenuCategoria> findByAtivoFalse();

    // Buscar categorias ordenadas pela ordem definida
    List<MenuCategoria> findAllByOrderByOrdemAsc();

    // Buscar categorias de um contexto ordenadas pela ordem
    List<MenuCategoria> findByContextoOrderByOrdemAsc(String contexto);

    // Buscar categorias criadas após uma data
    List<MenuCategoria> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar categorias criadas antes de uma data
    List<MenuCategoria> findByCriadoEmBefore(OffsetDateTime data);

    // Buscar categorias por nome (texto parcial)
    List<MenuCategoria> findByNomeContainingIgnoreCase(String nome);
}
