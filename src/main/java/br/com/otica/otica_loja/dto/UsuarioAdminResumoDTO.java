package br.com.otica.otica_loja.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UsuarioAdminResumoDTO(
        UUID id,
        String nome,
        String email,
        String telefone,
        Boolean ativo,
        OffsetDateTime atualizadoEm,
        boolean temAvaliacaoPendente,
        OffsetDateTime ultimaCompraEm
) {}