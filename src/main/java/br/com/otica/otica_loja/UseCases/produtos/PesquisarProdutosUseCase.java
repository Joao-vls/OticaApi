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
     * Pesquisa produtos por nome (busca parcial original).
     */
    public List<Produto> pesquisarPorNome(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return Collections.emptyList();
        }
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
     * 🔥 Pesquisa Global Completa (Barra de Busca Principal do Site)
     */
    public List<Produto> pesquisarPorTextoLivre(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return Collections.emptyList();
        }

        /*
         * MÁGICA DE UX: Troca espaços por '%'
         * Se o cliente digitar: "ray ban polarizado"
         * O Java transforma em: "ray%ban%polarizado"
         * Isso permite encontrar produtos mesmo que a ordem ou espaçamento sejam diferentes!
         */
        String termoOtimizado = termo.trim().replaceAll("\\s+", "%");

        return produtoRepository.pesquisaGlobalCompleta(termoOtimizado);
    }
}