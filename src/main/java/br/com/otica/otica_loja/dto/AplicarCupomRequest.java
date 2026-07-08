package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;

public record AplicarCupomRequest(
        String codigo,
        BigDecimal valorPedido,
        BigDecimal valorFrete
) {}