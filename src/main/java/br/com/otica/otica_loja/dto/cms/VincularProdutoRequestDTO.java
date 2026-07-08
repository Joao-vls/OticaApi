package br.com.otica.otica_loja.dto.cms;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record VincularProdutoRequestDTO(
        @NotNull(message = "O ID do produto é obrigatório")
        UUID produtoId,

        Integer ordem
) {}