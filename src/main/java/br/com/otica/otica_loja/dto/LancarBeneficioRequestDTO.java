package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.enums.TipoBeneficio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LancarBeneficioRequestDTO(
        @NotNull(message = "O tipo de benefício é obrigatório")
        TipoBeneficio tipo,

        @NotBlank(message = "A descrição do resgate é obrigatória")
        String descricao
) {}