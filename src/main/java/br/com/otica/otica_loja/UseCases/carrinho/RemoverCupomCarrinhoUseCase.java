package br.com.otica.otica_loja.UseCases.carrinho;

import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RemoverCupomCarrinhoUseCase {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Transactional
    public void remover(UUID usuarioId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado."));

        carrinho.setCupom(null);
        carrinhoRepository.save(carrinho);
    }
}