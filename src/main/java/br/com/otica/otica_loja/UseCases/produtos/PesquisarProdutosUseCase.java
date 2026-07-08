package br.com.otica.otica_loja.UseCases.produtos;


import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PesquisarProdutosUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Pesquisa produtos por nome (busca parcial).
     */
    public List<Produto> pesquisarPorNome(String termo) {
        return produtoRepository.findByNomeContainingIgnoreCase(termo);
    }

    /**
     * Pesquisa produtos por slug exato.
     */
    public Produto pesquisarPorSlug(String slug) {
        return produtoRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com este slug."));
    }

    /**
     * Pesquisa produtos por texto livre (nome + descrição).
     * Aqui você pode expandir para usar o campo searchVector com consultas nativas.
     */
    public List<Produto> pesquisarPorTextoLivre(String termo) {
        // Neste exemplo, reutilizamos a busca por nome.
        // Em produção, pode-se implementar uma query customizada com searchVector.
        return produtoRepository.findByNomeContainingIgnoreCase(termo);
    }
}
