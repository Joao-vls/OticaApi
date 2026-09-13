package br.com.otica.otica_loja.dto;



import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AplicarCupomRequest(
        String codigo,
        BigDecimal valorPedido,
        BigDecimal valorFrete,
        UUID usuarioId,
        List<UUID> produtosIds // Lista com os UUIDs dos produtos que estão no carrinho
) {}