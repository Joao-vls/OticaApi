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
public class ReservarEstoqueUseCase {

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    @Autowired
    private EstoqueMovimentacaoRepository movimentacaoRepository;

    /**
     * Reserva estoque de uma variante para um pedido/carrinho.
     * A reserva é registrada como uma movimentação de VENDA pendente.
     */
    public EstoqueMovimentacao reservar(UUID varianteId, Integer quantidade, UUID usuarioId, String observacao) {
        // 1. Buscar variante
        ProdutoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada."));

        // 2. Validar saldo disponível
        Integer saldoAnterior = variante.getStock();
        if (saldoAnterior < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente para reserva.");
        }

        // 3. Atualizar saldo (reduzindo temporariamente)
        Integer saldoAtual = saldoAnterior - quantidade;
        variante.setStock(saldoAtual);

        // 4. Criar movimentação de reserva (registrada como VENDA pendente)
        EstoqueMovimentacao movimentacao = new EstoqueMovimentacao();
        movimentacao.setVariante(variante);
        movimentacao.setTipo(TipoMovimentacao.VENDA); // usado como reserva
        movimentacao.setQuantidade(quantidade);
        movimentacao.setSaldoAnterior(saldoAnterior);
        movimentacao.setSaldoAtual(saldoAtual);
        movimentacao.setUsuarioId(usuarioId);
        movimentacao.setObservacao(observacao != null ? observacao : "Reserva de estoque para pedido/carrinho");
        movimentacao.setCriadoEm(OffsetDateTime.now());

        // 5. Persistir alterações
        varianteRepository.save(variante);
        return movimentacaoRepository.save(movimentacao);
    }
}
