package br.com.otica.otica_loja.UseCases.carrinho;

import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarCarrinhoUseCase {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    /**
     * Busca o carrinho de um usuário.
     */
    public Carrinho buscar(UUID usuarioId) {
        return carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado para este usuário."));
    }
}
