package br.com.otica.otica_loja.UseCases.checkout;

import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Entity.Carrinho.CarrinhoItem;
import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoItemRepository;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CalcularResumoPedidoUseCase {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private CarrinhoItemRepository carrinhoItemRepository;

    @Autowired
    private CupomRepository cupomRepository;

    /**
     * Calcula o resumo do pedido do usuário.
     */
    public ResumoPedido calcular(UUID usuarioId, String codigoCupom, BigDecimal valorFrete) {
        // 1. Buscar carrinho do usuário
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado para este usuário."));

        // 2. Buscar itens do carrinho
        List<CarrinhoItem> itens = carrinhoItemRepository.findByCarrinho(carrinho);

        // 3. Calcular subtotal
        BigDecimal subtotal = itens.stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Aplicar cupom (se informado)
        BigDecimal desconto = BigDecimal.ZERO;
        if (codigoCupom != null && !codigoCupom.isBlank()) {
            Cupom cupom = cupomRepository.findByCodigo(codigoCupom)
                    .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado."));

            switch (cupom.getTipo().toLowerCase()) {
                case "percentual":
                    desconto = subtotal.multiply(cupom.getValor().divide(BigDecimal.valueOf(100)));
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

            // Garantir que desconto não ultrapasse subtotal
            if (desconto.compareTo(subtotal) > 0) {
                desconto = subtotal;
            }
        }

        // 5. Calcular total
        BigDecimal total = subtotal.subtract(desconto).add(valorFrete);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        // 6. Retornar resumo
        return new ResumoPedido(subtotal, desconto, valorFrete, total);
    }

    /**
     * DTO para resumo do pedido.
     */
    public static class ResumoPedido {
        private BigDecimal subtotal;
        private BigDecimal desconto;
        private BigDecimal frete;
        private BigDecimal total;

        public ResumoPedido(BigDecimal subtotal, BigDecimal desconto, BigDecimal frete, BigDecimal total) {
            this.subtotal = subtotal;
            this.desconto = desconto;
            this.frete = frete;
            this.total = total;
        }

        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getDesconto() { return desconto; }
        public BigDecimal getFrete() { return frete; }
        public BigDecimal getTotal() { return total; }
    }
}
