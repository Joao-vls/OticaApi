package br.com.otica.otica_loja.UseCases.favoritos;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Avaliacao.Favorito;
import br.com.otica.otica_loja.Entity.Avaliacao.FavoritoId;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Avaliacao.FavoritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AdicionarFavoritoUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private FavoritoRepository favoritoRepository;

    /**
     * Adiciona um produto aos favoritos de um usuário.
     */
    public Favorito adicionar(UUID usuarioId, UUID produtoId) {
        // 1. Buscar usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // 2. Buscar produto
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        // 3. Criar chave composta
        FavoritoId favoritoId = new FavoritoId();
        favoritoId.setUsuarioId(usuarioId);
        favoritoId.setProdutoId(produtoId);

        // 4. Verificar duplicidade
        if (favoritoRepository.existsById(favoritoId)) {
            throw new IllegalArgumentException("Este produto já está nos favoritos do usuário.");
        }

        // 5. Criar favorito
        Favorito favorito = new Favorito();
        favorito.setId(favoritoId);
        favorito.setUsuario(usuario);
        favorito.setProduto(produto);
        favorito.setCriadoEm(OffsetDateTime.now());

        // 6. Persistir
        return favoritoRepository.save(favorito);
    }
}
