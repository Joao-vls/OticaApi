package br.com.otica.otica_loja.UseCases.carrinho;

import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Entity.Carrinho.CarrinhoItem;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoItemRepository;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoverItemCarrinhoUseCase {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private CarrinhoItemRepository carrinhoItemRepository;

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    /**
     * Remove um item específico do carrinho do usuário.
     */
    public void remover(UUID usuarioId, UUID varianteId) {
        // 1. Buscar carrinho do usuário
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado para este usuário."));

        // 2. Buscar variante do produto
        ProdutoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new IllegalArgumentException("Variante de produto não encontrada."));

        // 3. Buscar item no carrinho
        CarrinhoItem item = carrinhoItemRepository.findByCarrinhoAndVariante(carrinho, variante)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado no carrinho."));

        // 4. Remover item
        carrinhoItemRepository.delete(item);
    }
}
