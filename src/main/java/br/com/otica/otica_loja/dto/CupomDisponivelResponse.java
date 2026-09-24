package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record CupomDisponivelResponse(
        String codigo,
        String descricao,
        String tipo,
        BigDecimal valor,
        BigDecimal valorMinimoPedido,
        Integer quantidadeDisponivel, // Calculado dinamicamente
        Set<UUID> produtosIdsEspecificos,
        Set<UUID> categoriasIdsEspecificas
) {}