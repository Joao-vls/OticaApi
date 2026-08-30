package br.com.otica.otica_loja.dto;

public record CartaoPagamentoRequest(
        String tokenGeradoPeloFrontEnd,
        Integer parcelas,
        String paymentMethodId,
        String issuerId,
        String emailCliente,
        String nomeCliente,      // 👈 Adicionado
        String sobrenomeCliente  // 👈 Adicionado
) {
}