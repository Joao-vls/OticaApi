package br.com.otica.otica_loja.UseCases.avaliacoes;

import br.com.otica.otica_loja.Entity.Avaliacao.ProdutoAvaliacao;
import br.com.otica.otica_loja.Repository.Avaliacao.ProdutoAvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscarAvaliacoesPendentesUseCase {

    @Autowired
    private ProdutoAvaliacaoRepository avaliacaoRepository;

    /**
     * Busca todas as avaliações pendentes de aprovação (não aprovadas).
     */
    public List<ProdutoAvaliacao> buscarPendentes() {
        return avaliacaoRepository.findByAprovadoFalse();
    }
}
