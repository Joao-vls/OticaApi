package br.com.otica.otica_loja.dto;

import java.util.UUID;

public record BoletoPagamentoRequest(
        UUID pedidoId,
        UUID usuarioId,
        String emailCliente,
        String nomeCliente,
        String sobrenomeCliente,
        String cpfCliente
) {
}