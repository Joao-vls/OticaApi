package br.com.otica.otica_loja.dto;



import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CriarCupomRequest(
        String codigo,
        String descricao,
        String tipo,
        BigDecimal valor,
        BigDecimal valorMinimoPedido,
        Integer quantidadeTotal,
        OffsetDateTime dataInicio,
        OffsetDateTime dataFim,
        UUID usuarioIdEspecifico, // Opcional
        UUID produtoIdEspecifico, // Opcional
        Boolean usoUnico          // Opcional
) {}