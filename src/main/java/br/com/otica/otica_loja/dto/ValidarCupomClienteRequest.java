package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public  record ValidarCupomClienteRequest(
        String codigo,
        BigDecimal valorPedido,
        List<UUID> produtosIds
) {}