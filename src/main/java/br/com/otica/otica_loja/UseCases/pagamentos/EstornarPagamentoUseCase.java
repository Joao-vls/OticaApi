package br.com.otica.otica_loja.UseCases.pagamento;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.Pagamento;
import br.com.otica.otica_loja.Entity.Pedidos.Reembolso;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PagamentoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.ReembolsoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class EstornarPagamentoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ReembolsoRepository reembolsoRepository;

    /**
     * Estorna um pagamento já confirmado e cria um registro de reembolso.
     */
    public Reembolso estornarPagamento(UUID pedidoId, UUID pagamentoId, BigDecimal valor, UUID usuarioId, String motivo) {
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        // 2. Validar status atual
        if (pedido.getStatus() != StatusPedido.PAGO) {
            throw new IllegalArgumentException("Somente pedidos pagos podem ser estornados.");
        }

        // 3. Buscar pagamento
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));

        if (valor.compareTo(pagamento.getValor()) > 0) {
            throw new IllegalArgumentException("Valor do estorno não pode ser maior que o valor pago.");
        }

        // 4. Criar registro de reembolso
        Reembolso reembolso = new Reembolso();
        reembolso.setPagamento(pagamento);
        reembolso.setValor(valor);
        reembolso.setMotivo(motivo);
        reembolso.setCriadoEm(OffsetDateTime.now());

        // 5. Atualizar status do pedido para ESTORNADO
        pedido.setStatus(StatusPedido.ESTORNADO);
        pedido.setAtualizadoEm(OffsetDateTime.now());
        pedido.setObservacoes("Pagamento estornado | Usuário: " + usuarioId + " | Motivo: " + motivo);

        // 6. Persistir alterações
        pedidoRepository.save(pedido);
        return reembolsoRepository.save(reembolso);
    }
}
