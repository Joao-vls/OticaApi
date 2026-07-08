package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BannerRepository extends JpaRepository<Banner, UUID> {

    // Buscar banner pelo título
    Optional<Banner> findByTitulo(String titulo);

    // Buscar banners ativos
    List<Banner> findByAtivoTrue();

    // Buscar banners inativos
    List<Banner> findByAtivoFalse();

    // Buscar banners por posição (ex.: "home-top", "sidebar")
    List<Banner> findByPosicao(String posicao);

    // Buscar banners válidos dentro de um intervalo de datas
    List<Banner> findByDataInicioBeforeAndDataFimAfter(OffsetDateTime inicio, OffsetDateTime fim);

    // Buscar banners criados após uma data
    List<Banner> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar banners atualizados após uma data
    List<Banner> findByAtualizadoEmAfter(OffsetDateTime data);
}
