package br.com.otica.otica_loja.UseCases.carrinho;

import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import br.com.otica.otica_loja.UseCases.cupons.ValidarCupomUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AplicarCupomCarrinhoUseCase {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ValidarCupomUseCase validarCupomUseCase;

    @Transactional
    public void aplicar(UUID usuarioId, String codigoCupom) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado."));

        if (carrinho.getItens().isEmpty()) {
            throw new IllegalArgumentException("Adicione itens ao carrinho antes de aplicar um cupom.");
        }

        // Calcula o subtotal do carrinho para a validação
        BigDecimal subtotal = carrinho.getItens().stream()
                .map(i -> i.getPrecoUnitario().multiply(BigDecimal.valueOf(i.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Coleta os IDs dos produtos/variantes no carrinho
        List<UUID> produtosIds = carrinho.getItens().stream()
                .map(i -> i.getVariante().getId())
                .toList();

        // Valida o cupom usando sua regra de negócio existente
        Cupom cupom = validarCupomUseCase.validar(codigoCupom, subtotal, usuarioId, produtosIds);

        // Atrela o cupom ao carrinho e salva
        carrinho.setCupom(cupom);
        carrinhoRepository.save(carrinho);
    }
}