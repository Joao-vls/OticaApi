package br.com.otica.otica_loja.UseCases.avaliacoes;

import br.com.otica.otica_loja.Entity.Avaliacao.ProdutoAvaliacao;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Avaliacao.ProdutoAvaliacaoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarAvaliacoesProdutoUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoAvaliacaoRepository avaliacaoRepository;

    /**
     * Lista todas as avaliações de um produto.
     */
    public List<ProdutoAvaliacao> listarTodas(UUID produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        return avaliacaoRepository.findByProduto(produto);
    }

    /**
     * Lista apenas as avaliações aprovadas de um produto.
     */
    public List<ProdutoAvaliacao> listarAprovadas(UUID produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        return avaliacaoRepository.findByProdutoAndAprovadoTrue(produto);
    }

    /**
     * Lista apenas as avaliações pendentes (não aprovadas).
     */
    public List<ProdutoAvaliacao> listarPendentes(UUID produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        return avaliacaoRepository.findByProdutoAndAprovadoFalse(produto);
    }


    /**
     * Lista avaliações de um produto filtradas por nota.
     */
    public List<ProdutoAvaliacao> listarPorNota(UUID produtoId, Integer nota) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        return avaliacaoRepository.findByProdutoAndNota(produto, nota);
    }
}
