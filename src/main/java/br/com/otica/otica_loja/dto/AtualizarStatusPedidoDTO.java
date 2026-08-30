package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.enums.StatusPedido;

public record AtualizarStatusPedidoDTO(
        StatusPedido novoStatus,
        String observacao
) {}