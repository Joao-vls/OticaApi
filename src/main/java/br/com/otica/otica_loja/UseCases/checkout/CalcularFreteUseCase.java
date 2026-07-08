package br.com.otica.otica_loja.UseCases.checkout;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Repository.Auth.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CalcularFreteUseCase {

    @Autowired
    private EnderecoRepository enderecoRepository;

    /**
     * Calcula o frete para o pedido de um usuário.
     * Regras simples:
     * - Frete fixo base
     * - Adicional por distância (simulado pelo CEP)
     * - Frete grátis acima de valor mínimo
     */
    public BigDecimal calcular(UUID usuarioId, BigDecimal valorPedido) {
        // 1. Buscar endereço padrão do usuário
        Endereco endereco = enderecoRepository.findByUsuarioIdAndIsDefaultTrue(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Endereço padrão não encontrado para este usuário."));

        // 2. Definir regras de frete
        BigDecimal freteBase = BigDecimal.valueOf(20.00); // valor fixo base
        BigDecimal adicionalDistancia = BigDecimal.ZERO;

        // Exemplo simples: CEP começando com "39" (Norte de Minas) tem adicional
        if (endereco.getCep().startsWith("39")) {
            adicionalDistancia = BigDecimal.valueOf(10.00);
        }

        BigDecimal valorFrete = freteBase.add(adicionalDistancia);

        // 3. Frete grátis para pedidos acima de R$ 300
        if (valorPedido.compareTo(BigDecimal.valueOf(300.00)) >= 0) {
            valorFrete = BigDecimal.ZERO;
        }

        return valorFrete;
    }
}
