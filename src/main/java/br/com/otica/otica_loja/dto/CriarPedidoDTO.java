package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;

public record CriarPedidoDTO(

        BigDecimal valorFrete,
        String observacoes
) {}