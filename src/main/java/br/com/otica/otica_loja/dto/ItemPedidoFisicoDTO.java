package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedidoFisicoDTO(
        @NotNull(message = "O ID da variante do produto é obrigatório.")
        UUID varianteId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Min(value = 1, message = "A quantidade mínima é 1.")
        Integer quantidade,

        // Opcional: permite aplicar preço diferenciado/desconto direto no item. Se null, usa o preço cadastrado.
        BigDecimal precoUnitario
) {}