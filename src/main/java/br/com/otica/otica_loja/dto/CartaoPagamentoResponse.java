package br.com.otica.otica_loja.dto;

import java.util.UUID;

public record CartaoPagamentoResponse(
        UUID pedidoId,
        String status,
        String challengeUrl, // Será retornado para o front-end se o banco exigir verificação (3DS)
        String mensagem
) {
}