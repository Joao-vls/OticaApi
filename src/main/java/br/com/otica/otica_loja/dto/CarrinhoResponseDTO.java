package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CarrinhoResponseDTO(
        UUID id,
        UUID usuarioId,
        List<CarrinhoItemResponseDTO> itens,
        BigDecimal valorTotal,
        Integer quantidadeTotalItens,
        OffsetDateTime atualizadoEm
) {
    public static CarrinhoResponseDTO fromEntity(Carrinho carrinho, List<CarrinhoItemResponseDTO> itens) {
        BigDecimal total = itens.stream()
                .map(CarrinhoItemResponseDTO::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int qtdTotal = itens.stream()
                .mapToInt(CarrinhoItemResponseDTO::quantidade)
                .sum();

        return new CarrinhoResponseDTO(
                carrinho.getId(),
                carrinho.getUsuarioId(),
                itens,
                total,
                qtdTotal,
                carrinho.getAtualizadoEm()
        );
    }
}