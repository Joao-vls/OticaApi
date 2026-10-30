package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.UseCases.newsletter.DispararCampanhaPersonalizadaUseCase;
import br.com.otica.otica_loja.dto.EnviarCampanhaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/campanhas-email")
public class AdminCampanhaEmailController {

    @Autowired
    private DispararCampanhaPersonalizadaUseCase dispararCampanhaUseCase;

    // Disparar campanha de E-mail Marketing/Promoções
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/disparar")
    public ResponseEntity<String> dispararCampanha(@RequestBody EnviarCampanhaRequest request) {
        try {
            dispararCampanhaUseCase.executar(request);
            return ResponseEntity.ok("Campanha disparada com sucesso! Os e-mails estão sendo enviados em segundo plano.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ocorreu um erro interno ao disparar a campanha.");
        }
    }
}