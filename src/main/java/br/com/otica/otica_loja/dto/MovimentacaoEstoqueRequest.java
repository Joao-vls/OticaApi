package br.com.otica.otica_loja.dto;

import java.util.UUID;

public record MovimentacaoEstoqueRequest(
        UUID varianteId,
        Integer quantidade,
        UUID usuarioId,
        String observacao
) {
}
