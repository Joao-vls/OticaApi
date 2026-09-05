package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository; // 👈 Novo
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

    @Autowired
    private CarrinhoRepository carrinhoRepository; // 👈 Novo

    public Pedido confirmarPagamento(ConfirmarPagamentoRequest request) {
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.PROCESSANDO && pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido não está em um status válido para receber pagamento. Status atual: " + pedido.getStatus());
        }

        pedido.setStatus(StatusPedido.PAGO);
        pedido.setAtualizadoEm(OffsetDateTime.now());

        if ((pedido.getOrderIdMercadoPago() == null || pedido.getOrderIdMercadoPago().isBlank())
                && request.codigoTransacao() != null && !request.codigoTransacao().isBlank()) {
            pedido.setOrderIdMercadoPago(request.codigoTransacao());
        }

        String observacao = "Pagamento confirmado via " + request.metodoPagamento() +
                " | Código Transação (MP): " + request.codigoTransacao();

        if (request.usuarioId() != null) {
            observacao += " | Usuário/Admin aprovador: " + request.usuarioId();
        } else {
            observacao += " | Origem: Webhook Automático";
        }

        pedido.setObservacoes(observacao);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 👇 LIMPA O CARRINHO AQUI (PIX/BOLETO FOI PAGO!) 👇
        carrinhoRepository.findByUsuarioId(pedidoSalvo.getUsuarioId()).ifPresent(carrinho -> {
            carrinho.getItens().clear();
            carrinhoRepository.save(carrinho);
        });

        return pedidoSalvo;
    }
}