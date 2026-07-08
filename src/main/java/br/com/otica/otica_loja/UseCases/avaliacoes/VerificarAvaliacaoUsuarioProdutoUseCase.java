package br.com.otica.otica_loja.UseCases.avaliacoes;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Avaliacao.ProdutoAvaliacaoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VerificarAvaliacaoUsuarioProdutoUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoAvaliacaoRepository avaliacaoRepository;

    /**
     * Verifica se um usuário já avaliou um produto específico.
     */
    public boolean jaAvaliou(UUID produtoId, UUID usuarioId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        return avaliacaoRepository.existsByProdutoAndUsuarioId(produto, usuarioId);
    }
}
