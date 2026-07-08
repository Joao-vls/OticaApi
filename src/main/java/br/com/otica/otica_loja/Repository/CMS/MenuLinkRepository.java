package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.MenuLink;
import br.com.otica.otica_loja.Entity.CMS.MenuCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuLinkRepository extends JpaRepository<MenuLink, UUID> {

    // Buscar links de uma categoria específica
    List<MenuLink> findByCategoria(MenuCategoria categoria);

    // Buscar links ativos
    List<MenuLink> findByAtivoTrue();

    // Buscar links inativos
    List<MenuLink> findByAtivoFalse();

    // Buscar links ordenados pela ordem definida
    List<MenuLink> findAllByOrderByOrdemAsc();

    // Buscar links de uma categoria ordenados pela ordem
    List<MenuLink> findByCategoriaOrderByOrdemAsc(MenuCategoria categoria);

    // Buscar links por título (texto parcial)
    List<MenuLink> findByTituloContainingIgnoreCase(String titulo);

    // Verificar se já existe link com determinado título dentro de uma categoria
    boolean existsByCategoriaAndTitulo(MenuCategoria categoria, String titulo);
}
