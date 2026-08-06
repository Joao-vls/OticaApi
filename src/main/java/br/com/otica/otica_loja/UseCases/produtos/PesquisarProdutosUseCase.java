package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PesquisarProdutosUseCase {

    private final ProdutoRepository produtoRepository;

    /**
     * Pesquisa produtos por nome (busca parcial).
     */
    public List<Produto> pesquisarPorNome(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // Envia a palavra limpa sem concatenar '%'
        return produtoRepository.findByNomeContainingIgnoreCase(termo.trim());
    }

    /**
     * Pesquisa produtos por slug exato.
     */
    public Produto pesquisarPorSlug(String slug) {
        return produtoRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com este slug."));
    }

    /**
     * Pesquisa produtos por texto livre.
     */
    public List<Produto> pesquisarPorTextoLivre(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // Envia a palavra limpa sem concatenar '%'
        return produtoRepository.findByNomeContainingIgnoreCase(termo.trim());
    }
}