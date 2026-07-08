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
public class LiberarEstoqueUseCase {

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    @Autowired
    private EstoqueMovimentacaoRepository movimentacaoRepository;

    /**
     * Libera estoque previamente reservado ou vinculado a um pedido cancelado.
     */
    public EstoqueMovimentacao liberar(UUID varianteId, Integer quantidade, UUID usuarioId, String observacao) {
        // 1. Buscar variante
        ProdutoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada."));

        // 2. Calcular saldo
        Integer saldoAnterior = variante.getStock();
        Integer saldoAtual = saldoAnterior + quantidade;
        variante.setStock(saldoAtual);

        // 3. Criar movimentação de liberação (CANCELAMENTO)
        EstoqueMovimentacao movimentacao = new EstoqueMovimentacao();
        movimentacao.setVariante(variante);
        movimentacao.setTipo(TipoMovimentacao.CANCELAMENTO);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setSaldoAnterior(saldoAnterior);
        movimentacao.setSaldoAtual(saldoAtual);
        movimentacao.setUsuarioId(usuarioId);
        movimentacao.setObservacao(observacao != null ? observacao : "Liberação de estoque por cancelamento");
        movimentacao.setCriadoEm(OffsetDateTime.now());

        // 4. Persistir alterações
        varianteRepository.save(variante);
        return movimentacaoRepository.save(movimentacao);
    }
}
