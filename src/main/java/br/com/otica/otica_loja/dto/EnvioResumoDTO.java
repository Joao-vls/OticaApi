package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.enums.StatusPedido;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record EnvioResumoDTO(
        UUID envioId,
        String codigoRastreio,
        String transportadoraNome,
        Long pedidoNumero,
        StatusPedido statusPedido,
        String clienteNome,
        String clienteEmail,
        OffsetDateTime enviadoEm,
        OffsetDateTime entregueEm,
        List<ItemResumoDTO> itens // 🎯 NOVO: Lista de itens do envio
) {}