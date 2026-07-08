package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarProdutosDestaqueUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Lista todos os produtos em destaque.
     */
    public List<Produto> listarEmDestaque() {
        return produtoRepository.findByDestaqueTrue();
    }
}
