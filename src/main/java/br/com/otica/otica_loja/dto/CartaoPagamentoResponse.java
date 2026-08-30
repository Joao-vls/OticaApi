package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartaoPagamentoResponse(
        UUID pedidoId,
        BigDecimal valor,
        String transactionId,
        String status,
        String challengeUrl, // 👈 URL para o iframe do 3DS se houver challenge
        String mensagem
) {
}