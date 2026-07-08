package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.Campanha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CampanhaRepository extends JpaRepository<Campanha, UUID> {

    // Buscar campanha pelo slug (único)
    Optional<Campanha> findBySlug(String slug);

    // Verificar se já existe campanha com determinado slug
    boolean existsBySlug(String slug);

    // Buscar campanhas ativas
    List<Campanha> findByAtivoTrue();

    // Buscar campanhas inativas
    List<Campanha> findByAtivoFalse();

    // Buscar campanhas criadas após uma data
    List<Campanha> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar campanhas atualizadas após uma data
    List<Campanha> findByAtualizadoEmAfter(OffsetDateTime data);

    // Buscar campanhas válidas dentro de um intervalo de datas
    List<Campanha> findByDataInicioBeforeAndDataFimAfter(OffsetDateTime inicio, OffsetDateTime fim);

    // Buscar campanhas por nome (texto parcial)
    List<Campanha> findByNomeContainingIgnoreCase(String nome);
}
