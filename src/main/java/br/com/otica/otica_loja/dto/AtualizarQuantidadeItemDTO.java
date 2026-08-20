package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AtualizarQuantidadeItemDTO(
        @NotNull(message = "A quantidade é obrigatória.")
        @Min(value = 1, message = "A quantidade deve ser de no mínimo 1.")
        Integer quantidade
) {}