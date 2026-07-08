package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.PaginaCMS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaginaCMSRepository extends JpaRepository<PaginaCMS, UUID> {

    // Buscar página pelo slug (único)
    Optional<PaginaCMS> findBySlug(String slug);

    // Verificar se já existe página com determinado slug
    boolean existsBySlug(String slug);

    // Buscar páginas publicadas
    List<PaginaCMS> findByPublicadoTrue();

    // Buscar páginas não publicadas
    List<PaginaCMS> findByPublicadoFalse();

    // Buscar páginas criadas após uma data
    List<PaginaCMS> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar páginas atualizadas após uma data
    List<PaginaCMS> findByAtualizadoEmAfter(OffsetDateTime data);

    // Buscar páginas publicadas após uma data
    List<PaginaCMS> findByPublicadoEmAfter(OffsetDateTime data);

    // Buscar páginas por título (texto parcial)
    List<PaginaCMS> findByTituloContainingIgnoreCase(String titulo);
}
