package br.com.otica.otica_loja.UseCases.pedidos;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarPedidosAdminUseCase {

    private final PedidoRepository pedidoRepository;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPorStatus(StatusPedido status) {
        return pedidoRepository.findAll().stream()
                .filter(p -> p.getStatus() == status)
                .toList();
    }

    public List<Pedido> listarPorValorMinimo(BigDecimal valor) {
        return pedidoRepository.findAll().stream()
                .filter(p -> p.getTotal().compareTo(valor) >= 0)
                .toList();
    }

    public List<Pedido> listarCriadosDepois(OffsetDateTime data) {
        return pedidoRepository.findAll().stream()
                .filter(p -> p.getCriadoEm().isAfter(data))
                .toList();
    }

    public Page<Pedido> buscarPaginado(String busca, StatusPedido status, Pageable pageable) {
        return pedidoRepository.buscarComFiltrosPaginado(busca, status, pageable);
    }
}