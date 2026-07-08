package br.com.otica.otica_loja.UseCases.avaliacoes;

import br.com.otica.otica_loja.Entity.Avaliacao.ProdutoAvaliacao;
import br.com.otica.otica_loja.Repository.Avaliacao.ProdutoAvaliacaoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeletarAvaliacaoUsuarioUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoAvaliacaoRepository avaliacaoRepository;

    /**
     * Permite que o usuário delete a própria avaliação de um produto.
     */
    public void deletar(UUID produtoId, UUID usuarioId) {
        var produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        ProdutoAvaliacao avaliacao = avaliacaoRepository.findByProdutoAndUsuarioId(produto, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada para este usuário e produto."));

        // Verifica se a avaliação realmente pertence ao usuário
        if (!avaliacao.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("Usuário não autorizado a excluir esta avaliação.");
        }

        avaliacaoRepository.delete(avaliacao);
    }
}
