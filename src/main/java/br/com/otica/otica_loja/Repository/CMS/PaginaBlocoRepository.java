package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.PaginaBloco;
import br.com.otica.otica_loja.Entity.CMS.PaginaCMS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaginaBlocoRepository extends JpaRepository<PaginaBloco, UUID> {

    // Buscar blocos de uma página específica
    List<PaginaBloco> findByPagina(PaginaCMS pagina);

    // Buscar blocos de uma página ordenados pela ordem
    List<PaginaBloco> findByPaginaOrderByOrdemAsc(PaginaCMS pagina);

    // Buscar blocos por tipo (ex.: hero, vitrine, banner, etc.)
    List<PaginaBloco> findByTipo(String tipo);

    // Buscar blocos ativos (se futuramente tiver campo ativo)
    // List<PaginaBloco> findByAtivoTrue();

    // Verificar se já existe bloco de determinado tipo em uma página
    boolean existsByPaginaAndTipo(PaginaCMS pagina, String tipo);
}
