package br.com.otica.otica_loja.UseCases.avaliacoes;

import br.com.otica.otica_loja.Entity.Avaliacao.ProdutoAvaliacao;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Avaliacao.ProdutoAvaliacaoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BuscarAvaliacaoUsuarioProdutoUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoAvaliacaoRepository avaliacaoRepository;

    /**
     * Busca a avaliação de um usuário para um produto específico.
     */
    public Optional<ProdutoAvaliacao> buscar(UUID produtoId, UUID usuarioId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        return avaliacaoRepository.findByProdutoAndUsuarioId(produto, usuarioId);
    }
}
