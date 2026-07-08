package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.FooterGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FooterGrupoRepository extends JpaRepository<FooterGrupo, UUID> {

    // Buscar grupos ativos
    List<FooterGrupo> findByAtivoTrue();

    // Buscar grupos inativos
    List<FooterGrupo> findByAtivoFalse();

    // Buscar grupos ordenados pela ordem definida
    List<FooterGrupo> findAllByOrderByOrdemAsc();

    // Buscar grupos por título (texto parcial)
    List<FooterGrupo> findByTituloContainingIgnoreCase(String titulo);

    // Verificar se já existe grupo com determinado título
    boolean existsByTitulo(String titulo);
}
