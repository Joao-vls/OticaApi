package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record EnderecoDTO(
        UUID id,

        @NotBlank(message = "O nome do recebedor é obrigatório")
        String nomeRecebedor,

        @NotBlank(message = "O telefone do recebedor é obrigatório")
        String telefoneRecebedor,

        @NotBlank(message = "O CEP é obrigatório")
        String cep,

        @NotBlank(message = "O logradouro é obrigatório")
        String logradouro,

        @NotBlank(message = "O número é obrigatório")
        String numero,

        String complemento,

        @NotBlank(message = "O bairro é obrigatório")
        String bairro,

        @NotBlank(message = "A cidade é obrigatória")
        String cidade,

        @NotBlank(message = "O estado é obrigatório")
        String estado,

        String pais,

        @NotNull(message = "Defina se o endereço é o padrão")
        Boolean isDefault
) {}