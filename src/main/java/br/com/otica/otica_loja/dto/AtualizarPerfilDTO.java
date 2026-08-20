package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.Size;

public record AtualizarPerfilDTO(
        @Size(max = 100) String nome,
        @Size(max = 20) String telefone,
        @Size(max = 50) String username,
        String dataNascimento,
        String cpf,
        String genero
) {}