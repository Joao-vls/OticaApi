package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ConfirmarPagamentoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Confirma o pagamento de um pedido e atualiza o status para PAGO.
     */
    public Pedido confirmarPagamento(UUID pedidoId, UUID usuarioId, String metodoPagamento, String codigoTransacao) {
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        // 2. Validar status atual
        if (pedido.getStatus() != StatusPedido.PROCESSANDO) {
            throw new IllegalArgumentException("Pedido não está em processamento de pagamento.");
        }

        // 3. Atualizar status para PAGO
        pedido.setStatus(StatusPedido.PAGO);
        pedido.setAtualizadoEm(OffsetDateTime.now());

        // 4. Registrar observações com dados do pagamento
        String observacao = "Pagamento confirmado via " + metodoPagamento +
                " | Código Transação: " + codigoTransacao +
                " | Usuário: " + usuarioId;
        pedido.setObservacoes(observacao);

        // 5. Persistir alterações
        return pedidoRepository.save(pedido);
    }
}
