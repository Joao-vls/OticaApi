package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.enums.MetodoPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CriarPedidoFisicoRequest(
        // 👇 Se ele já for cadastrado, usamos o ID.
        UUID clienteId,

        // 👇 Se não for cadastrado, preenchemos os dados abaixo:
        String clienteNome,
        String clienteCpf,
        String clienteTelefone,
        String clienteEmail,

        @NotEmpty(message = "O pedido deve conter pelo menos um item.")
        @Valid
        List<ItemPedidoFisicoDTO> itens,

        @NotNull(message = "O método de pagamento é obrigatório.")
        MetodoPagamento metodoPagamento,

        BigDecimal valorRecebido,
        BigDecimal descontoManual,
        String codigoCupom,
        String observacoes,
        Boolean prontaEntrega
) {}