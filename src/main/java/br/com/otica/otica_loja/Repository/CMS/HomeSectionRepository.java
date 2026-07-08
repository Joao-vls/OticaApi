package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.HomeSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HomeSectionRepository extends JpaRepository<HomeSection, UUID> {

    // Buscar seção pelo identificador único
    Optional<HomeSection> findByIdentificador(String identificador);

    // Verificar se já existe seção com determinado identificador
    boolean existsByIdentificador(String identificador);

    // Buscar seções ativas
    List<HomeSection> findByAtivoTrue();

    // Buscar seções inativas
    List<HomeSection> findByAtivoFalse();

    // Buscar seções por tipo (hero, banner, vitrine, etc.)
    List<HomeSection> findByTipo(String tipo);

    // Buscar seções ordenadas pela ordem definida
    List<HomeSection> findAllByOrderByOrdemAsc();

    // Buscar seções criadas após uma data
    List<HomeSection> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar seções atualizadas após uma data
    List<HomeSection> findByAtualizadoEmAfter(OffsetDateTime data);
}
