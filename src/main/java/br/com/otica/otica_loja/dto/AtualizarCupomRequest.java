package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AtualizarCupomRequest(
        String codigo,
        String descricao,
        String tipo,
        BigDecimal valor,
        BigDecimal valorMinimoPedido,
        Integer quantidadeTotal,
        OffsetDateTime dataInicio,
        OffsetDateTime dataFim,
        Boolean ativo
) {}