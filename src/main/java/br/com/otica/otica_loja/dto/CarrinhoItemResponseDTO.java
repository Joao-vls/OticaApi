package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.Entity.Carrinho.CarrinhoItem;

import java.math.BigDecimal;
import java.util.UUID;

public record CarrinhoItemResponseDTO(
        UUID id,
        UUID varianteId,
        String sku,
        String nomeProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
    public static CarrinhoItemResponseDTO fromEntity(CarrinhoItem item) {
        BigDecimal subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));

        return new CarrinhoItemResponseDTO(
                item.getId(),
                item.getVariante().getId(),
                item.getVariante().getSku(),
                item.getVariante().getProduto() != null ? item.getVariante().getProduto().getNome() : null,
                item.getQuantidade(),
                item.getPrecoUnitario(),
                subtotal
        );
    }
}