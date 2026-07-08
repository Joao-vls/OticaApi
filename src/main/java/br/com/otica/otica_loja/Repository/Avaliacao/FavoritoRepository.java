package br.com.otica.otica_loja.Repository.Avaliacao;

import br.com.otica.otica_loja.Entity.Avaliacao.Favorito;
import br.com.otica.otica_loja.Entity.Avaliacao.FavoritoId;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {

    // Buscar todos os favoritos de um usuário
    List<Favorito> findByUsuario(Usuario usuario);

    // Buscar todos os favoritos de um produto
    List<Favorito> findByProduto(Produto produto);

    // Verificar se um produto já está favoritado por um usuário
    boolean existsByUsuarioAndProduto(Usuario usuario, Produto produto);

    // Remover favorito de um usuário para um produto específico
    void deleteByUsuarioAndProduto(Usuario usuario, Produto produto);
}
