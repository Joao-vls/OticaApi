package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AtualizarCupomRequest(
        String codigo,
        String descricao,
        String tipo,
        BigDecimal valor,
        BigDecimal valorMinimoPedido,
        Integer quantidadeTotal,
        OffsetDateTime dataInicio,
        OffsetDateTime dataFim,
        Boolean ativo,
        List<UUID> usuariosIdsEspecificos,
        List<UUID> produtosIdsEspecificos,
        Boolean usoUnico
) {}