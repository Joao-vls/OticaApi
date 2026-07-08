package br.com.otica.otica_loja.Repository.FAQ;

import br.com.otica.otica_loja.Entity.FAQ.FaqCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FaqCategoriaRepository extends JpaRepository<FaqCategoria, UUID> {

    // Buscar categoria pelo título
    Optional<FaqCategoria> findByTitulo(String titulo);

    // Verificar se já existe categoria com determinado título
    boolean existsByTitulo(String titulo);

    // Buscar categorias ativas
    List<FaqCategoria> findByAtivoTrue();

    // Buscar categorias inativas
    List<FaqCategoria> findByAtivoFalse();

    // Buscar categorias ordenadas por ordem
    List<FaqCategoria> findAllByOrderByOrdemAsc();

    // Buscar categorias criadas após uma data
    List<FaqCategoria> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar categorias atualizadas após uma data
    List<FaqCategoria> findByAtualizadoEmAfter(OffsetDateTime data);
}
