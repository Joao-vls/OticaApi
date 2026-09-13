package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AplicarCupomUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    /**
     * Aplica o cupom, validando todas as regras (limite, usuário, produto).
     */
    public BigDecimal aplicar(String codigo, BigDecimal valorPedido, BigDecimal valorFrete,
                              UUID usuarioRequisitanteId, List<UUID> produtosNoPedidoIds) {

        Cupom cupom = cupomRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado."));

        // 1. Validações Básicas
        if (!cupom.getAtivo()) {
            throw new IllegalArgumentException("Este cupom está inativo ou já foi utilizado.");
        }

        // 2. Validação de Limite de Usos
        if (cupom.getQuantidadeTotal() != null && cupom.getQuantidadeUtilizada() >= cupom.getQuantidadeTotal()) {
            throw new IllegalArgumentException("O limite de usos para este cupom já foi atingido.");
        }

        // 3. Validação de Usuário Específico
        if (cupom.getUsuarioIdEspecifico() != null && !cupom.getUsuarioIdEspecifico().equals(usuarioRequisitanteId)) {
            throw new IllegalArgumentException("Este cupom não pertence a este usuário.");
        }

        // 4. Validação de Produto Específico
        if (cupom.getProdutoIdEspecifico() != null && (produtosNoPedidoIds == null || !produtosNoPedidoIds.contains(cupom.getProdutoIdEspecifico()))) {
            throw new IllegalArgumentException("Este cupom só é válido para um produto específico que não está no carrinho.");
        }

        // 5. Cálculo do Desconto
        BigDecimal desconto = BigDecimal.ZERO;

        switch (cupom.getTipo().toLowerCase()) {
            case "percentual":
                desconto = valorPedido.multiply(cupom.getValor().divide(BigDecimal.valueOf(100)));
                break;
            case "fixo":
                desconto = cupom.getValor();
                break;
            case "frete":
                desconto = valorFrete.min(cupom.getValor());
                break;
            default:
                throw new IllegalArgumentException("Tipo de cupom inválido.");
        }

        BigDecimal valorFinal = valorPedido.subtract(desconto);
        if (valorFinal.compareTo(BigDecimal.ZERO) < 0) {
            valorFinal = BigDecimal.ZERO;
        }

        // 6. Atualiza uso e desativa se for Uso Único
        cupom.setQuantidadeUtilizada(cupom.getQuantidadeUtilizada() + 1);

        if (Boolean.TRUE.equals(cupom.getUsoUnico())) {
            cupom.setAtivo(false); // "Exclui" logicamente o cupom após o uso
        } else if (cupom.getQuantidadeTotal() != null && cupom.getQuantidadeUtilizada() >= cupom.getQuantidadeTotal()) {
            cupom.setAtivo(false); // Inativa também se bater o limite máximo
        }

        cupomRepository.save(cupom);

        return valorFinal;
    }
}