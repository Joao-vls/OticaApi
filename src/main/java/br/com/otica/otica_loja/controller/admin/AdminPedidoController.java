package br.com.otica.otica_loja.controller.admin;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/pedidos")
public class AdminPedidoController {

    // Lista todos os pedidos
    @GetMapping
    public String listarPedidos() {
        return "lista de pedidos";
    }

    // Atualiza o status de um pedido específico
    @PatchMapping("/{id}/status")
    public String atualizarStatus(@PathVariable Long id, @RequestBody String novoStatus) {
        return "pedido " + id + " atualizado para status: " + novoStatus;
    }
}
