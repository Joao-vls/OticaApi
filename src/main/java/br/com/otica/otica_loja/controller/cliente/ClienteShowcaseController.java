package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.UseCases.cms.CurtirShowcaseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cliente/showcase")
@RequiredArgsConstructor
public class ClienteShowcaseController {

    private final CurtirShowcaseUseCase curtirShowcaseUseCase;

    @PostMapping("/{id}/curtir")
    public ResponseEntity<?> curtirShowcase(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable UUID id) {

        // Garante que a requisição tem um token de usuário válido
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            curtirShowcaseUseCase.executar(id, usuarioLogado.getId());
            return ResponseEntity.ok(Map.of("message", "Showcase curtido com sucesso!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao curtir showcase: " + e.getMessage()));
        }
    }
}