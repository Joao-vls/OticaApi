package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.Entity.Carrinho.CarrinhoItem;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import com.fasterxml.jackson.annotation.JsonRawValue; // Importação necessária

import java.math.BigDecimal;
import java.util.UUID;

public record CarrinhoItemResponseDTO(
        UUID id,
        UUID varianteId,
        String sku,
        String nomeProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal,
        String cor,

        @JsonRawValue // Garante que a String JSON seja entregue como um objeto no frontend
        String specs,

        String imageUrl
) {
    public static CarrinhoItemResponseDTO fromEntity(CarrinhoItem item) {
        BigDecimal subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));

        ProdutoVariante variante = item.getVariante();
        Produto produto = variante.getProduto();

        // 1. Extraindo a Imagem
        String imageUrl = null;
        if (variante.getColorImagePath() != null) {
            imageUrl = variante.getColorImagePath();
        }
        // else if (variante.getMidias() != null && !variante.getMidias().isEmpty()) {
        //     imageUrl = variante.getMidias().get(0).getPath();
        // }

        // 2. Extraindo as Specs do Produto Pai
        String jsonSpecs = produto != null ? produto.getSpecs() : null;

        return new CarrinhoItemResponseDTO(
                item.getId(),
                variante.getId(),
                variante.getSku(),
                produto != null ? produto.getNome() : null,
                item.getQuantidade(),
                item.getPrecoUnitario(),
                subtotal,
                variante.getColorName(),
                jsonSpecs, // Passando a string JSON completa
                imageUrl
        );
    }
}