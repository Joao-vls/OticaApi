package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record EstornarPagamentoRequestDTO(
        @NotNull UUID pedidoId,
        @NotNull String orderIdMercadoPago,
        String transactionIdMercadoPago,
        BigDecimal valor,
        String motivo
) {}