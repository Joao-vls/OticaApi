package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.enums.TipoEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarEmailTemplateDTO(
        @NotNull TipoEmail tipo,
        @NotBlank String assunto,
        @NotBlank String htmlBase,
        Boolean ativo
) {}