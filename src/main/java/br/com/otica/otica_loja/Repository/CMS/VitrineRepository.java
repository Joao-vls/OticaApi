package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VitrineRepository extends JpaRepository<Vitrine, UUID> {

    // Buscar vitrine pelo slug (único)
    Optional<Vitrine> findBySlug(String slug);

    // Verificar se já existe vitrine com determinado slug
    boolean existsBySlug(String slug);

    // Buscar vitrines ativas
    List<Vitrine> findByAtivoTrue();

    // Buscar vitrines inativas
    List<Vitrine> findByAtivoFalse();

    // Buscar vitrines ordenadas pela ordem definida
    List<Vitrine> findAllByOrderByOrdemAsc();

    // Buscar vitrines criadas após uma data
    List<Vitrine> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar vitrines atualizadas após uma data
    List<Vitrine> findByAtualizadoEmAfter(OffsetDateTime data);

    // Buscar vitrines por nome (texto parcial)
    List<Vitrine> findByNomeContainingIgnoreCase(String nome);

    // Buscar vitrines por título (texto parcial)
    List<Vitrine> findByTituloContainingIgnoreCase(String titulo);
}
