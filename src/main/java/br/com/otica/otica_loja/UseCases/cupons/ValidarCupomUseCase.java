package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class ValidarCupomUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    /**
     * Valida um cupom pelo código e valor do pedido.
     */
    public Cupom validar(String codigo, BigDecimal valorPedido) {
        Cupom cupom = cupomRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado."));

        // 1. Verificar se está ativo
        if (!cupom.getAtivo()) {
            throw new IllegalArgumentException("Cupom inativo.");
        }

        // 2. Verificar período de validade
        OffsetDateTime agora = OffsetDateTime.now();
        if (cupom.getDataInicio() != null && agora.isBefore(cupom.getDataInicio())) {
            throw new IllegalArgumentException("Cupom ainda não está válido.");
        }
        if (cupom.getDataFim() != null && agora.isAfter(cupom.getDataFim())) {
            throw new IllegalArgumentException("Cupom expirado.");
        }

        // 3. Verificar quantidade disponível
        if (cupom.getQuantidadeTotal() != null &&
                cupom.getQuantidadeUtilizada() >= cupom.getQuantidadeTotal()) {
            throw new IllegalArgumentException("Cupom já foi totalmente utilizado.");
        }

        // 4. Verificar valor mínimo do pedido
        if (cupom.getValorMinimoPedido() != null &&
                valorPedido.compareTo(cupom.getValorMinimoPedido()) < 0) {
            throw new IllegalArgumentException("Valor mínimo do pedido não atingido.");
        }

        // Se passou em todas as validações, retorna o cupom válido
        return cupom;
    }
}
