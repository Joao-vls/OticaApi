package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.dto.ConfirmarPagamentoRequest;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class ConfirmarPagamentoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Confirma o pagamento de um pedido e atualiza o status para PAGO.
     */
    public Pedido confirmarPagamento(ConfirmarPagamentoRequest request) {
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        // 2. Validar status atual (Aceita PROCESSANDO ou AGUARDANDO_PAGAMENTO)
        if (pedido.getStatus() != StatusPedido.PROCESSANDO && pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido não está em um status válido para receber pagamento. Status atual: " + pedido.getStatus());
        }

        // 3. Atualizar status para PAGO
        pedido.setStatus(StatusPedido.PAGO);
        pedido.setAtualizadoEm(OffsetDateTime.now());

        // 4. Salvar orderIdMercadoPago caso ainda esteja nulo no pedido e tenha sido informado na requisição
        if ((pedido.getOrderIdMercadoPago() == null || pedido.getOrderIdMercadoPago().isBlank())
                && request.codigoTransacao() != null && !request.codigoTransacao().isBlank()) {
            pedido.setOrderIdMercadoPago(request.codigoTransacao());
        }

        // 5. Registrar observações com dados do pagamento
        String observacao = "Pagamento confirmado via " + request.metodoPagamento() +
                " | Código Transação (MP): " + request.codigoTransacao();

        if (request.usuarioId() != null) {
            observacao += " | Usuário/Admin aprovador: " + request.usuarioId();
        } else {
            observacao += " | Origem: Webhook Automático";
        }

        pedido.setObservacoes(observacao);

        // 6. Persistir alterações
        return pedidoRepository.save(pedido);
    }
}