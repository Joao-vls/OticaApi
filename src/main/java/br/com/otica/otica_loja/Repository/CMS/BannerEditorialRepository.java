package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.BannerEditorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BannerEditorialRepository extends JpaRepository<BannerEditorial, UUID> {

    // Buscar banner editorial pelo identificador único
    Optional<BannerEditorial> findByIdentificador(String identificador);

    // Verificar se já existe banner editorial com determinado identificador
    boolean existsByIdentificador(String identificador);

    // --- NOVOS MÉTODOS POR LAYOUT TIPO (HORIZONTAL / VERTICAL) ---

    // Busca o primeiro banner (mais recente cadastrado) pelo tipo de layout
    Optional<BannerEditorial> findFirstByLayoutTipoOrderByIdDesc(String layoutTipo);

    // Busca o primeiro banner ATIVO pelo tipo de layout
    Optional<BannerEditorial> findFirstByLayoutTipoAndAtivoTrueOrderByIdDesc(String layoutTipo);

    // Lista todos os banners filtrados pelo tipo de layout
    List<BannerEditorial> findByLayoutTipo(String layoutTipo);

    // --- MÉTODOS DE FILTRO GERAIS ---

    // Buscar banners editoriais ativos
    List<BannerEditorial> findByAtivoTrue();

    // Buscar banners editoriais inativos
    List<BannerEditorial> findByAtivoFalse();

    // Buscar banners editoriais atualizados após uma data
    List<BannerEditorial> findByAtualizadoEmAfter(OffsetDateTime data);

    // Buscar banners editoriais atualizados antes de uma data
    List<BannerEditorial> findByAtualizadoEmBefore(OffsetDateTime data);
}