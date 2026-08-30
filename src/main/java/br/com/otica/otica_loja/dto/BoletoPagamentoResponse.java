package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BoletoPagamentoResponse(
        UUID pedidoId,
        BigDecimal valor,
        String boletoUrl, // Link do PDF do boleto
        String linhaDigitavel,
        String mensagem
) {
}