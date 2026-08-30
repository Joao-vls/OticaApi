package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.UseCases.pedidos.*;

import br.com.otica.otica_loja.dto.CancelarPedidoDTO;
import br.com.otica.otica_loja.dto.CriarPedidoDTO;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cliente/pedidos")
public class ClientePedidoController {

    @Autowired
    private CriarPedidoUseCase criarPedidoUseCase;

    @Autowired
    private ListarPedidosUsuarioUseCase listarPedidosUsuarioUseCase;

    @Autowired
    private BuscarPedidoUseCase buscarPedidoUseCase;

    @Autowired
    private CancelarPedidoUseCase cancelarPedidoUseCase;

    @PostMapping
    public ResponseEntity<?> criarPedido(@AuthenticationPrincipal Usuario usuarioLogado,
                                         @RequestBody CriarPedidoDTO dto) {
        if (usuarioLogado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            Pedido pedido = criarPedidoUseCase.criar(
                    usuarioLogado.getId(),
                    dto.codigoCupom(),
                    dto.valorFrete() != null ? dto.valorFrete() : java.math.BigDecimal.ZERO,
                    dto.observacoes()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarMeusPedidos(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(required = false) StatusPedido status) {

        if (usuarioLogado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Pedido> pedidos = (status != null)
                ? listarPedidosUsuarioUseCase.listarPorStatus(usuarioLogado.getId(), status)
                : listarPedidosUsuarioUseCase.listarTodos(usuarioLogado.getId());

        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarMeuPedido(@AuthenticationPrincipal Usuario usuarioLogado,
                                             @PathVariable UUID id) {
        if (usuarioLogado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            Pedido pedido = buscarPedidoUseCase.buscarPorId(id);

            // Segurança: Garantir que o pedido pertence ao usuário logado
            if (!pedido.getUsuarioId().equals(usuarioLogado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Acesso negado a este pedido."));
            }

            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@AuthenticationPrincipal Usuario usuarioLogado,
                                            @PathVariable UUID id,
                                            @RequestBody(required = false) CancelarPedidoDTO dto) {
        if (usuarioLogado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            // Primeiro busca para garantir que pertence ao usuário
            Pedido pedido = buscarPedidoUseCase.buscarPorId(id);
            if (!pedido.getUsuarioId().equals(usuarioLogado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Você não tem permissão para cancelar este pedido."));
            }

            String observacao = (dto != null && dto.observacao() != null) ? dto.observacao() : "Cancelado pelo cliente";
            Pedido pedidoCancelado = cancelarPedidoUseCase.cancelar(id, usuarioLogado.getId(), observacao);

            return ResponseEntity.ok(pedidoCancelado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}