package br.com.otica.otica_loja.Repository.Pedidos;

import br.com.otica.otica_loja.Entity.Pedidos.Pagamento;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.enums.MetodoPagamento;
import br.com.otica.otica_loja.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {

    // Buscar pagamentos de um pedido específico
    List<Pagamento> findByPedido(Pedido pedido);

    // Buscar pagamento pelo gatewayTransactionId
    Optional<Pagamento> findByGatewayTransactionId(String gatewayTransactionId);

    // Buscar pagamentos por método de pagamento
    List<Pagamento> findByMetodo(MetodoPagamento metodo);

    // Buscar pagamentos por status
    List<Pagamento> findByStatus(StatusPagamento status);

    // Buscar pagamentos acima de um valor
    List<Pagamento> findByValorGreaterThan(BigDecimal valor);

    // Buscar pagamentos criados após uma data
    List<Pagamento> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar pagamentos atualizados após uma data
    List<Pagamento> findByAtualizadoEmAfter(OffsetDateTime data);

    // Verificar se já existe pagamento para um pedido
    boolean existsByPedido(Pedido pedido);
}
