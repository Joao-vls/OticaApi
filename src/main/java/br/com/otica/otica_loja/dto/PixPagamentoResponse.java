package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PixPagamentoResponse(
        UUID pedidoId,
        BigDecimal valor,
        String chavePix,
        String qrCodeBase64,
        String mensagem
) {
}