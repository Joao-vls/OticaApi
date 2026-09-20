package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransportadoraRequest(
        @NotBlank(message = "O nome da transportadora é obrigatório.")
        String nome,

        @NotBlank(message = "O código/sigla é obrigatório.")
        @Size(max = 100, message = "O código não pode exceder 100 caracteres.")
        String codigo,

        @NotNull(message = "O status (ativo) é obrigatório.")
        Boolean ativo
) {}