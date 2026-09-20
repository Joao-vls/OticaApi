package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CriarEnvioRequest(
        @NotNull(message = "O ID do pedido é obrigatório")
        UUID pedidoId,

        @NotNull(message = "O ID da transportadora é obrigatório")
        UUID transportadoraId,

        String codigoRastreio,
        String urlRastreio,

        @NotNull(message = "O valor do frete é obrigatório")
        BigDecimal valorFrete,

        String observacao
) {}