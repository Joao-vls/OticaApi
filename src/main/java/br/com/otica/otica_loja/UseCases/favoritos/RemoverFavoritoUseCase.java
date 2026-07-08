package br.com.otica.otica_loja.UseCases.favoritos;

import br.com.otica.otica_loja.Entity.Avaliacao.FavoritoId;
import br.com.otica.otica_loja.Repository.Avaliacao.FavoritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoverFavoritoUseCase {

    @Autowired
    private FavoritoRepository favoritoRepository;

    /**
     * Remove um produto dos favoritos de um usuário.
     */
    public void remover(UUID usuarioId, UUID produtoId) {
        FavoritoId favoritoId = new FavoritoId();
        favoritoId.setUsuarioId(usuarioId);
        favoritoId.setProdutoId(produtoId);

        if (!favoritoRepository.existsById(favoritoId)) {
            throw new IllegalArgumentException("Este produto não está nos favoritos do usuário.");
        }

        favoritoRepository.deleteById(favoritoId);
    }
}
