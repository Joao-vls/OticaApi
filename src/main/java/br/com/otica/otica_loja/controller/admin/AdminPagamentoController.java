package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.Reembolso;
import br.com.otica.otica_loja.UseCases.pagamentos.CancelarPagamentoUseCase;
import br.com.otica.otica_loja.UseCases.pagamentos.ConfirmarPagamentoUseCase;
import br.com.otica.otica_loja.UseCases.pagamentos.EstornarPagamentoUseCase;
import br.com.otica.otica_loja.UseCases.pedidos.ListarPedidosAdminUseCase;
import br.com.otica.otica_loja.dto.CancelarPagamentoRequest;
import br.com.otica.otica_loja.dto.ConfirmarPagamentoRequest;
import br.com.otica.otica_loja.dto.EstornarPagamentoRequestDTO;
import br.com.otica.otica_loja.enums.StatusPedido;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/pagamentos")
public class AdminPagamentoController {

    @Autowired
    private ListarPedidosAdminUseCase listarPedidosAdminUseCase;

    @Autowired
    private ConfirmarPagamentoUseCase confirmarPagamentoUseCase;

    @Autowired
    private CancelarPagamentoUseCase cancelarPagamentoUseCase;

    @Autowired
    private EstornarPagamentoUseCase estornarPagamentoUseCase;

    // 1. Listar todos os pedidos e seus status de pagamento - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @GetMapping("/pedidos")
    public ResponseEntity<List<Pedido>> listarTodosPedidos() {
        List<Pedido> pedidos = listarPedidosAdminUseCase.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    // 2. Listar pedidos filtrando por status (ex: PAGO, PROCESSANDO, CANCELADO) - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @GetMapping("/pedidos/status/{status}")
    public ResponseEntity<List<Pedido>> listarPorStatus(@PathVariable StatusPedido status) {
        List<Pedido> pedidos = listarPedidosAdminUseCase.listarPorStatus(status);
        return ResponseEntity.ok(pedidos);
    }

    // 3. Listar pedidos acima de determinado valor - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @GetMapping("/pedidos/valor-minimo")
    public ResponseEntity<List<Pedido>> listarPorValorMinimo(@RequestParam BigDecimal valor) {
        List<Pedido> pedidos = listarPedidosAdminUseCase.listarPorValorMinimo(valor);
        return ResponseEntity.ok(pedidos);
    }

    // 4. Listar pedidos criados após uma data específica - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @GetMapping("/pedidos/criados-depois")
    public ResponseEntity<List<Pedido>> listarCriadosDepois(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime data) {
        List<Pedido> pedidos = listarPedidosAdminUseCase.listarCriadosDepois(data);
        return ResponseEntity.ok(pedidos);
    }

    // 5. Confirmar manualmente um pagamento (Aprovação manual/baixa no balcão) - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @PostMapping("/confirmar")
    public ResponseEntity<Pedido> confirmarPagamentoManualmente(@Valid @RequestBody ConfirmarPagamentoRequest request) {
        Usuario adminAutenticado = getUsuarioAutenticado();

        // Sobrescreve/Garante que o ID do admin seja o do usuário logado na sessão
        ConfirmarPagamentoRequest requestComAdmin = new ConfirmarPagamentoRequest(
                request.pedidoId(),
                adminAutenticado.getId(),
                request.metodoPagamento(),
                request.codigoTransacao()
        );

        Pedido pedidoAtualizado = confirmarPagamentoUseCase.confirmarPagamento(requestComAdmin);
        return ResponseEntity.ok(pedidoAtualizado);
    }

    // 6. Cancelar um pagamento/pedido - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @PostMapping("/cancelar")
    public ResponseEntity<Pedido> cancelarPagamento(@Valid @RequestBody CancelarPagamentoRequest request) {
        Usuario adminAutenticado = getUsuarioAutenticado();

        CancelarPagamentoRequest requestComAdmin = new CancelarPagamentoRequest(
                request.pedidoId(),
                adminAutenticado.getId(),
                request.estorno(),
                request.motivo(),
                request.idPagamentoMercadoPago()
        );

        Pedido pedidoCancelado = cancelarPagamentoUseCase.cancelarPagamento(requestComAdmin);
        return ResponseEntity.ok(pedidoCancelado);
    }

    // 7. Processar estorno total ou parcial via Mercado Pago Orders API - EXCLUSIVO ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/estornar")
    public ResponseEntity<Reembolso> estornarPagamento(@Valid @RequestBody EstornarPagamentoRequestDTO request) {
        Usuario adminAutenticado = getUsuarioAutenticado();

        Reembolso reembolso = estornarPagamentoUseCase.estornarPagamento(
                request.pedidoId(),
                request.orderIdMercadoPago(),
                request.transactionIdMercadoPago(),
                request.valor(),
                adminAutenticado.getId(),
                request.motivo()
        );

        return ResponseEntity.ok(reembolso);
    }

    /**
     * Auxiliar para resgatar o objeto Usuario autenticado do Security Context
     */
    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Usuario usuario) {
            return usuario;
        }
        throw new IllegalStateException("Nenhum usuário autenticado encontrado no contexto de segurança.");
    }
}