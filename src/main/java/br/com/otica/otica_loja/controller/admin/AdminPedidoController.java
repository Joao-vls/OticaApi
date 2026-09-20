package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.UseCases.pedidos.IniciarSeparacaoUseCase;
import br.com.otica.otica_loja.UseCases.pedidos.ListarPedidosAdminUseCase; // 👈 Import necessário
import br.com.otica.otica_loja.enums.StatusPedido;                   // 👈 Import necessário
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/pedidos")
@RequiredArgsConstructor
public class AdminPedidoController {

    private final IniciarSeparacaoUseCase iniciarSeparacaoUseCase;
    private final ListarPedidosAdminUseCase listarPedidosAdminUseCase; // 👈 Injetado via Lombok

    /**
     * Move um pedido do status PAGO para SEPARACAO.
     */
    @PatchMapping("/{pedidoId}/iniciar-separacao")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<Pedido> iniciarSeparacao(@PathVariable UUID pedidoId) {
        Pedido pedidoAtualizado = iniciarSeparacaoUseCase.iniciarSeparacao(pedidoId);
        return ResponseEntity.ok(pedidoAtualizado);
    }

    /**
     * 🎯 NOVO: Lista pedidos filtrando por status (Ex: SEPARACAO) para o select do painel de logística.
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<List<Pedido>> listarPorStatus(@PathVariable StatusPedido status) {
        List<Pedido> pedidos = listarPedidosAdminUseCase.listarPorStatus(status);
        return ResponseEntity.ok(pedidos);
    }

}