package br.com.otica.otica_loja.UseCases.estoque;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Entity.Estoque.EstoqueMovimentacao;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.Repository.Estoque.EstoqueMovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConsultarEstoqueUseCase {

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    @Autowired
    private EstoqueMovimentacaoRepository movimentacaoRepository;

    /**
     * Consulta o saldo atual de estoque de uma variante.
     */
    public Integer consultarSaldo(UUID varianteId) {
        ProdutoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada."));
        return variante.getStock();
    }

    /**
     * Consulta o histórico de movimentações de uma variante.
     */
    public List<EstoqueMovimentacao> consultarHistorico(UUID varianteId) {
        ProdutoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada."));
        return movimentacaoRepository.findByVariante(variante);
    }
}
