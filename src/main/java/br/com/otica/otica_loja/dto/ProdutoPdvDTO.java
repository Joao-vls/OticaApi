package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoPdvDTO(
        UUID varianteId,
        String nomeCompleto,
        String sku,
        Integer estoque,
        BigDecimal precoFinal
) {}