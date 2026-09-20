package br.com.otica.otica_loja.UseCases.pedidos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IniciarSeparacaoUseCase {

    private final PedidoRepository pedidoRepository;

    public Pedido iniciarSeparacao(UUID pedidoId) {
        // 1. Busca o pedido no banco
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        // 2. Valida se o status atual permite iniciar a separação
        if (pedido.getStatus() != StatusPedido.PAGO) {
            throw new IllegalArgumentException("Apenas pedidos com status PAGO podem ir para separação. Status atual: " + pedido.getStatus());
        }

        // 3. Atualiza o status e a data de modificação
        pedido.setStatus(StatusPedido.SEPARACAO);
        pedido.setAtualizadoEm(OffsetDateTime.now());

        // 4. Adiciona uma observação de histórico (opcional, mas recomendado)
        String novaObservacao = "Separação iniciada em " + OffsetDateTime.now().toLocalDate();
        if (pedido.getObservacoes() != null && !pedido.getObservacoes().isBlank()) {
            pedido.setObservacoes(pedido.getObservacoes() + " | " + novaObservacao);
        } else {
            pedido.setObservacoes(novaObservacao);
        }

        // 5. Salva e retorna o pedido atualizado
        return pedidoRepository.save(pedido);
    }
}