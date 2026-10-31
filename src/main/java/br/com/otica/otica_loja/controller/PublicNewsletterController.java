package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.UseCases.newsletter.InscreverNewsletterUseCase;
import br.com.otica.otica_loja.dto.InscreverNewsletterRequest;
import br.com.otica.otica_loja.service.auth.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public/newsletter")
public class PublicNewsletterController {

    @Autowired
    private InscreverNewsletterUseCase inscreverNewsletterUseCase;

    @Autowired
    private RateLimitService rateLimitService;

    @PostMapping("/inscrever")
    public ResponseEntity<?> inscrever(@Valid @RequestBody InscreverNewsletterRequest request, HttpServletRequest httpRequest) {

        String ip = rateLimitService.obterIpCliente(httpRequest);

        // Verifica a regra de 2 inscrições por semana
        if (!rateLimitService.resolveNewsletterBucket(ip).tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Limite de inscrições excedido para esta rede. Tente novamente na próxima semana."));
        }

        try {
            // O UseCase já valida se o e-mail existe no banco (lança IllegalArgumentException)
            inscreverNewsletterUseCase.inscrever(request.email(), "SITE_PÚBLICO");

            return ResponseEntity.ok(Map.of("message", "Inscrição realizada com sucesso! Você receberá nossas novidades."));

        } catch (IllegalArgumentException e) {
            // Caso o e-mail já esteja inscrito
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Ocorreu um erro interno ao processar a inscrição."));
        }
    }
}