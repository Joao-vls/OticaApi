package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CriarAvaliacaoDTO(
        @NotNull(message = "O ID do produto é obrigatório.")
        UUID produtoId,

        @NotNull(message = "A nota é obrigatória.")
        @Min(value = 1, message = "A nota mínima é 1.")
        @Max(value = 5, message = "A nota máxima é 5.")
        Integer nota,

        @Size(max = 100, message = "O título deve ter no máximo 100 caracteres.")
        String titulo,

        @Size(max = 1000, message = "O texto deve ter no máximo 1000 caracteres.")
        String texto,

        String imagemPath
) {}