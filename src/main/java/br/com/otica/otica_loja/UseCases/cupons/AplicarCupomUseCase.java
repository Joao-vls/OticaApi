package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class AplicarCupomUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    @Autowired
    private ProdutoVarianteRepository produtoVarianteRepository;

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

        // 3. Validação de Usuários Específicos
        if (cupom.getUsuariosIdsEspecificos() != null && !cupom.getUsuariosIdsEspecificos().isEmpty()) {
            if (!cupom.getUsuariosIdsEspecificos().contains(usuarioRequisitanteId)) {
                throw new IllegalArgumentException("Este cupom não está disponível para o seu usuário.");
            }
        }

        // 4. Validação de Produtos Específicos (Incluindo checagem de Variante)
        if (cupom.getProdutosIdsEspecificos() != null && !cupom.getProdutosIdsEspecificos().isEmpty()) {
            if (produtosNoPedidoIds == null || produtosNoPedidoIds.isEmpty()) {
                throw new IllegalArgumentException("Nenhum produto válido no carrinho para este cupom.");
            }

            // Verifica se ALGUM produto do pedido bate com o cupom (ID direto ou produto pai da variante)
            boolean temProdutoValido = produtosNoPedidoIds.stream()
                    .anyMatch(idCarrinho -> {
                        if (cupom.getProdutosIdsEspecificos().contains(idCarrinho)) return true;

                        return produtoVarianteRepository.findById(idCarrinho)
                                .map(variante -> variante.getProduto() != null &&
                                        cupom.getProdutosIdsEspecificos().contains(variante.getProduto().getId()))
                                .orElse(false);
                    });

            if (!temProdutoValido) {
                throw new IllegalArgumentException("Este cupom só é válido para produtos específicos que não estão no carrinho.");
            }
        }

        // 5. Cálculo do Desconto SEGURO (Prevenindo ArithmeticException)
        BigDecimal desconto = BigDecimal.ZERO;

        switch (cupom.getTipo().toLowerCase()) {
            case "percentual":
                desconto = valorPedido.multiply(cupom.getValor())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                break;
            case "fixo":
                desconto = cupom.getValor().min(valorPedido);
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