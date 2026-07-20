package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // 🔥 Substitui o @Autowired usando injeção via construtor do Lombok
public class BuscarProdutoPorSlugUseCase {

    private final ProdutoRepository produtoRepository;

    /**
     * Executa a busca de um produto pelo slug para o catálogo da loja.
     * Renomeado para 'executar' para bater com a chamada do Controller.
     */
    public Produto executar(String slug) {
        return produtoRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com o slug: " + slug));
    }
}