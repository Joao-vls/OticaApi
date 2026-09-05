package br.com.otica.otica_loja.dto;

import java.util.UUID;

public record CartaoPagamentoRequest(
        UUID pedidoId,
        String paymentMethodId,
        String tokenGeradoPeloFrontEnd,
        Integer parcelas,
        String emailCliente,
        String nomeCliente,
        String sobrenomeCliente
) {
}