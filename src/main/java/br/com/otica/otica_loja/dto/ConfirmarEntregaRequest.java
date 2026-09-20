package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmarEntregaRequest(
        @NotBlank(message = "A localização da entrega é obrigatória")
        String localizacao,

        String observacao
) {}