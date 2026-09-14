package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.UseCases.cupons.ValidarCupomUseCase;


import br.com.otica.otica_loja.dto.ValidarCupomClienteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cliente/cupons")
public class ClienteCupomController {

    @Autowired
    private ValidarCupomUseCase validarCupomUseCase;

    // Usamos POST porque enviar listas (produtosIds) via GET é má prática.
    @PostMapping("/validar")
    public ResponseEntity<?> validarCupomParaCliente(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody ValidarCupomClienteRequest request) {

        if (usuarioLogado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            Cupom cupom = validarCupomUseCase.validar(
                    request.codigo(),
                    request.valorPedido(),
                    usuarioLogado.getId(),
                    request.produtosIds()
            );
            return ResponseEntity.ok(cupom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}