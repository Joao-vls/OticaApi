package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.FooterLink;
import br.com.otica.otica_loja.Entity.CMS.FooterGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FooterLinkRepository extends JpaRepository<FooterLink, UUID> {

    // Buscar links de um grupo específico
    List<FooterLink> findByGrupo(FooterGrupo grupo);

    // Buscar links ativos
    List<FooterLink> findByAtivoTrue();

    // Buscar links inativos
    List<FooterLink> findByAtivoFalse();

    // Buscar links ordenados pela ordem definida
    List<FooterLink> findAllByOrderByOrdemAsc();

    // Buscar links de um grupo ordenados pela ordem
    List<FooterLink> findByGrupoOrderByOrdemAsc(FooterGrupo grupo);

    // Buscar links por título (texto parcial)
    List<FooterLink> findByTituloContainingIgnoreCase(String titulo);

    // Verificar se já existe link com determinado título dentro de um grupo
    boolean existsByGrupoAndTitulo(FooterGrupo grupo, String titulo);
}
