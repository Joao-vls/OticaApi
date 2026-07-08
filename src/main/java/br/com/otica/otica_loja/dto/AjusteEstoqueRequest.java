package br.com.otica.otica_loja.dto;

import java.util.UUID;

public record AjusteEstoqueRequest(
        UUID varianteId,
        Integer novoSaldo,
        UUID usuarioId,
        String observacao
) {
}
