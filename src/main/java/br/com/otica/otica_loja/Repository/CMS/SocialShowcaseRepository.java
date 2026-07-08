package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.SocialShowcase;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SocialShowcaseRepository extends JpaRepository<SocialShowcase, UUID> {

    // Buscar showcases ativos
    List<SocialShowcase> findByAtivoTrue();

    // Buscar showcases inativos
    List<SocialShowcase> findByAtivoFalse();

    // Buscar showcases por produto
    List<SocialShowcase> findByProduto(Produto produto);

    // Buscar showcases por marca
    List<SocialShowcase> findByMarcaNomeContainingIgnoreCase(String marcaNome);

    // Buscar showcases por modelo
    List<SocialShowcase> findByModeloNomeContainingIgnoreCase(String modeloNome);

    // Buscar showcases por username
    List<SocialShowcase> findByUsernameContainingIgnoreCase(String username);

    // Buscar showcases ordenados pela ordem
    List<SocialShowcase> findAllByOrderByOrdemAsc();

    // Buscar showcases criados após uma data
    List<SocialShowcase> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar showcases com mais de X visualizações
    List<SocialShowcase> findByVisualizacoesGreaterThan(Long minVisualizacoes);

    // Buscar showcases com mais de X curtidas
    List<SocialShowcase> findByCurtidasGreaterThan(Long minCurtidas);
}
