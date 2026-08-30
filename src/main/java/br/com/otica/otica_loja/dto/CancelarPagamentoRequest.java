package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CancelarPagamentoRequest(
        @NotNull(message = "O ID do pedido é obrigatório.")
        UUID pedidoId,

        UUID usuarioId, // Quem solicitou o cancelamento (preenchido via contexto de segurança)

        boolean estorno,

        String motivo,

        String idPagamentoMercadoPago // String para suportar IDs no formato "ORD..." ou "PAY..."
) {
}