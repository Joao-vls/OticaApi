package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record MovimentacaoEstoqueRequest(
        UUID varianteId,
        Integer quantidade,
        UUID usuarioId,
        String observacao
) {

}
