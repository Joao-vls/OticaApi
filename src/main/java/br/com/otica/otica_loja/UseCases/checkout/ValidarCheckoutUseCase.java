package br.com.otica.otica_loja.UseCases.checkout;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Entity.Carrinho.CarrinhoItem;
import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Auth.EnderecoRepository;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoItemRepository;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ValidarCheckoutUseCase {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private CarrinhoItemRepository carrinhoItemRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private CupomRepository cupomRepository;

    /**
     * Valida se o checkout pode ser realizado.
     */
    public ResultadoValidacao validar(UUID usuarioId, String codigoCupom, BigDecimal valorFrete) {
        // 1. Verificar carrinho
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado para este usuário."));

        List<CarrinhoItem> itens = carrinhoItemRepository.findByCarrinho(carrinho);
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Carrinho está vazio.");
        }

        // 2. Verificar endereço padrão
        Endereco endereco = enderecoRepository.findByUsuarioIdAndIsDefaultTrue(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Endereço padrão não encontrado."));

        // 3. Calcular subtotal
        BigDecimal subtotal = itens.stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Validar cupom (se informado)
        BigDecimal desconto = BigDecimal.ZERO;
        if (codigoCupom != null && !codigoCupom.isBlank()) {
            Cupom cupom = cupomRepository.findByCodigo(codigoCupom)
                    .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado."));

            if (!cupom.getAtivo()) {
                throw new IllegalArgumentException("Cupom inativo.");
            }

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
        }

        // 5. Calcular total
        BigDecimal total = subtotal.subtract(desconto).add(valorFrete);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        return new ResultadoValidacao(true, subtotal, desconto, valorFrete, total, endereco);
    }

    /**
     * DTO para resultado da validação.
     */
    public static class ResultadoValidacao {
        private boolean valido;
        private BigDecimal subtotal;
        private BigDecimal desconto;
        private BigDecimal frete;
        private BigDecimal total;
        private Endereco enderecoEntrega;

        public ResultadoValidacao(boolean valido, BigDecimal subtotal, BigDecimal desconto,
                                  BigDecimal frete, BigDecimal total, Endereco enderecoEntrega) {
            this.valido = valido;
            this.subtotal = subtotal;
            this.desconto = desconto;
            this.frete = frete;
            this.total = total;
            this.enderecoEntrega = enderecoEntrega;
        }

        public boolean isValido() { return valido; }
        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getDesconto() { return desconto; }
        public BigDecimal getFrete() { return frete; }
        public BigDecimal getTotal() { return total; }
        public Endereco getEnderecoEntrega() { return enderecoEntrega; }
    }
}
