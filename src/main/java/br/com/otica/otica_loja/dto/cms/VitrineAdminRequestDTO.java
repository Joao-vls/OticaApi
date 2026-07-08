package br.com.otica.otica_loja.dto.cms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VitrineAdminRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String nome,

        @NotBlank(message = "O slug é obrigatório")
        @Size(max = 150)
        String slug,

        @Size(max = 255)
        String titulo,

        String subtitulo,

        Integer ordem,
        Boolean ativo
) {}