package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AplicarCupomUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    /**
     * Aplica o cupom ao valor do pedido e retorna o valor final com desconto.
     */
    public BigDecimal aplicar(String codigo, BigDecimal valorPedido, BigDecimal valorFrete) {
        Cupom cupom = cupomRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado."));

        // Primeiro validar cupom (pode usar ValidarCupomUseCase antes)
        if (!cupom.getAtivo()) {
            throw new IllegalArgumentException("Cupom inativo.");
        }

        BigDecimal desconto = BigDecimal.ZERO;

        switch (cupom.getTipo().toLowerCase()) {
            case "percentual":
                desconto = valorPedido.multiply(cupom.getValor().divide(BigDecimal.valueOf(100)));
                break;

            case "fixo":
                desconto = cupom.getValor();
                break;

            case "frete":
                desconto = valorFrete.min(cupom.getValor()); // desconto limitado ao valor do frete
                break;

            default:
                throw new IllegalArgumentException("Tipo de cupom inválido.");
        }

        // Garantir que o desconto não ultrapasse o valor do pedido
        BigDecimal valorFinal = valorPedido.subtract(desconto);
        if (valorFinal.compareTo(BigDecimal.ZERO) < 0) {
            valorFinal = BigDecimal.ZERO;
        }

        // Atualizar quantidade utilizada
        cupom.setQuantidadeUtilizada(cupom.getQuantidadeUtilizada() + 1);
        cupomRepository.save(cupom);

        return valorFinal;
    }
}
