package br.com.otica.otica_loja.UseCases.avaliacoes;

import br.com.otica.otica_loja.Entity.Avaliacao.ProdutoAvaliacao;
import br.com.otica.otica_loja.Repository.Avaliacao.ProdutoAvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReprovarAvaliacaoUseCase {

    @Autowired
    private ProdutoAvaliacaoRepository avaliacaoRepository;

    /**
     * Reprova uma avaliação de produto.
     */
    public ProdutoAvaliacao reprovar(UUID avaliacaoId) {
        ProdutoAvaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada."));

        if (!avaliacao.getAprovado()) {
            throw new IllegalArgumentException("Esta avaliação já está pendente ou reprovada.");
        }

        avaliacao.setAprovado(false);
        return avaliacaoRepository.save(avaliacao);
    }
}
