package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;

public record CriarPedidoDTO(
        String codigoCupom,
        BigDecimal valorFrete,
        String observacoes
) {}