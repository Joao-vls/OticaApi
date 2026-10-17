package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.enums.MetodoPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CriarPedidoFisicoRequest(
        // Opcional: ID do cliente cadastrado. Se nulo, associa ao consumidor final
        UUID clienteId,

        @NotEmpty(message = "O pedido deve conter pelo menos um item.")
        @Valid
        List<ItemPedidoFisicoDTO> itens,

        @NotNull(message = "O método de pagamento é obrigatório.")
        MetodoPagamento metodoPagamento,

        // Valor entregue pelo cliente (para calcular troco em dinheiro)
        BigDecimal valorRecebido,

        // Desconto manual em R$ concedido no balcão
        BigDecimal descontoManual,

        // Cupom de desconto opcional
        String codigoCupom,

        // Ex: "Venda Balcão", "Cliente aguarda montagem de lentes"
        String observacoes,

        // true = Pronta entrega (Status PAGO) | false = Requer montagem/lab (Status SEPARACAO)
        Boolean prontaEntrega
) {}