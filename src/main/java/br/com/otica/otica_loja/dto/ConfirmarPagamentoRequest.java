package br.com.otica.otica_loja.dto;

import java.util.UUID;

public record ConfirmarPagamentoRequest(
        UUID pedidoId,
        UUID usuarioId, // Pode ser null se a confirmação vier do Webhook
        String metodoPagamento,
        String codigoTransacao
) {
}