package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarTelefoneDTO(
        @NotBlank(message = "O telefone é obrigatório.")
        @Size(min = 8, max = 20, message = "Telefone inválido.")
        String novoTelefone
) {}