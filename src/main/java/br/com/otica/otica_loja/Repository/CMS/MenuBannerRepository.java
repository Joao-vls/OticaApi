package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.MenuBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MenuBannerRepository extends JpaRepository<MenuBanner, UUID> {

    // Buscar banners por contexto (ex.: sol, grau, institucional, geral)
    List<MenuBanner> findByContexto(String contexto);

    // Buscar banners ativos
    List<MenuBanner> findByAtivoTrue();

    // Buscar banners inativos
    List<MenuBanner> findByAtivoFalse();

    // Buscar banners ordenados pela ordem definida
    List<MenuBanner> findAllByOrderByOrdemAsc();

    // Buscar banners de um contexto ordenados pela ordem
    List<MenuBanner> findByContextoOrderByOrdemAsc(String contexto);

    // Buscar banners criados após uma data
    List<MenuBanner> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar banners criados antes de uma data
    List<MenuBanner> findByCriadoEmBefore(OffsetDateTime data);
}
