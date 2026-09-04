package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.UseCases.dashboard.RegistrarAcessoUseCase;
import br.com.otica.otica_loja.dto.dashboard.RegistrarLogAcessoDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/log-acesso")
public class PublicLogAcessoController {

    @Autowired
    private RegistrarAcessoUseCase registrarAcessoUseCase;

    @PostMapping
    public ResponseEntity<Void> registrarAcesso(
            @RequestBody RegistrarLogAcessoDTO dto,
            HttpServletRequest request) {

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        } else {
            clientIp = clientIp.split(",")[0].trim();
        }

        dto.setIp(clientIp);

        registrarAcessoUseCase.executar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}