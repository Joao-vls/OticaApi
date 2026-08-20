package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record FavoritoDTO(
        @NotNull(message = "O ID do produto é obrigatório.")
        UUID produtoId
) {}