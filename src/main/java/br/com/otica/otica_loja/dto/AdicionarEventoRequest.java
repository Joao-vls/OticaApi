package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.NotBlank;

public record AdicionarEventoRequest(
        @NotBlank(message = "A descrição do evento é obrigatória")
        String descricao,

        String localizacao
) {}