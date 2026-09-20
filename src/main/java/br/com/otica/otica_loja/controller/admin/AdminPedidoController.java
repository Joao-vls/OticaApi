package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.UseCases.pedidos.IniciarSeparacaoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/pedidos")
@RequiredArgsConstructor
public class AdminPedidoController {

    private final IniciarSeparacaoUseCase iniciarSeparacaoUseCase;

    /**
     * Move um pedido do status PAGO para SEPARACAO.
     * O uso do verbo PATCH é ideal aqui pois estamos apenas modificando o estado do recurso.
     */
    @PatchMapping("/{pedidoId}/iniciar-separacao")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<Pedido> iniciarSeparacao(@PathVariable UUID pedidoId) {

        Pedido pedidoAtualizado = iniciarSeparacaoUseCase.iniciarSeparacao(pedidoId);

        return ResponseEntity.ok(pedidoAtualizado);
    }

}