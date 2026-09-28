package br.com.otica.otica_loja.dto;

import java.util.UUID;

public record SocialShowcaseRequestDTO(
        UUID produtoId,
        String marcaNome,
        String modeloNome,
        String username,
        Integer ordem,
        Boolean ativo
) {}