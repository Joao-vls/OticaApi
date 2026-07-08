package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuscarProdutoPorSlugUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Busca um produto pelo slug.
     */
    public Produto buscarPorSlug(String slug) {
        return produtoRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com este slug."));
    }
}
