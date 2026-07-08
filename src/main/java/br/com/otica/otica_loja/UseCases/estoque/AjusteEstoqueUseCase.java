package br.com.otica.otica_loja.UseCases.estoque;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Entity.Estoque.EstoqueMovimentacao;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.Repository.Estoque.EstoqueMovimentacaoRepository;
import br.com.otica.otica_loja.enums.TipoMovimentacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AjusteEstoqueUseCase {

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    @Autowired
    private EstoqueMovimentacaoRepository movimentacaoRepository;

    /**
     * Registra um ajuste de estoque para uma variante.
     * O ajuste define diretamente o novo saldo.
     */
    public EstoqueMovimentacao registrarAjuste(UUID varianteId, Integer novoSaldo, UUID usuarioId, String observacao) {
        // 1. Buscar variante
        ProdutoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada."));

        // 2. Calcular diferença
        Integer saldoAnterior = variante.getStock();
        Integer quantidadeAjustada = novoSaldo - saldoAnterior;

        // 3. Atualizar saldo
        variante.setStock(novoSaldo);

        // 4. Criar movimentação
        EstoqueMovimentacao movimentacao = new EstoqueMovimentacao();
        movimentacao.setVariante(variante);
        movimentacao.setTipo(TipoMovimentacao.AJUSTE);
        movimentacao.setQuantidade(quantidadeAjustada);
        movimentacao.setSaldoAnterior(saldoAnterior);
        movimentacao.setSaldoAtual(novoSaldo);
        movimentacao.setUsuarioId(usuarioId);
        movimentacao.setObservacao(observacao);
        movimentacao.setCriadoEm(OffsetDateTime.now());

        // 5. Persistir alterações
        varianteRepository.save(variante);
        return movimentacaoRepository.save(movimentacao);
    }
}
