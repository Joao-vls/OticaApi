package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CancelarPagamentoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Cancela ou estorna o pagamento de um pedido.
     */
    public Pedido cancelarPagamento(UUID pedidoId, UUID usuarioId, boolean estorno, String motivo) {
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        // 2. Validar status atual
        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.ESTORNADO) {
            throw new IllegalArgumentException("Pedido já está cancelado/estornado.");
        }

        // 3. Atualizar status
        if (estorno && pedido.getStatus() == StatusPedido.PAGO) {
            pedido.setStatus(StatusPedido.ESTORNADO);
        } else {
            pedido.setStatus(StatusPedido.CANCELADO);
        }

        pedido.setAtualizadoEm(OffsetDateTime.now());

        // 4. Registrar observações
        String observacao = (estorno ? "Pagamento estornado" : "Pagamento cancelado") +
                " | Usuário: " + usuarioId +
                " | Motivo: " + motivo;
        pedido.setObservacoes(observacao);

        // 5. Persistir alterações
        return pedidoRepository.save(pedido);
    }
}
