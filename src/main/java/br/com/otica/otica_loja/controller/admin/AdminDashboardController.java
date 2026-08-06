package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.UseCases.dashboard.ObterAcessosPorHorarioUseCase;
import br.com.otica.otica_loja.UseCases.dashboard.ObterAtividadeGridUseCase;
import br.com.otica.otica_loja.UseCases.dashboard.ObterUltimasVendasUseCase;
import br.com.otica.otica_loja.UseCases.dashboard.RegistrarAcessoUseCase;
import br.com.otica.otica_loja.dto.dashboard.AcessoHorarioDTO;
import br.com.otica.otica_loja.dto.dashboard.DiaVendaDTO;
import br.com.otica.otica_loja.dto.dashboard.RegistrarLogAcessoDTO;
import br.com.otica.otica_loja.dto.dashboard.UltimaVendaDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    @Autowired
    private ObterAtividadeGridUseCase obterAtividadeGridUseCase;

    @Autowired
    private ObterUltimasVendasUseCase obterUltimasVendasUseCase;

    @Autowired
    private ObterAcessosPorHorarioUseCase obterAcessosPorHorarioUseCase;

    @Autowired
    private RegistrarAcessoUseCase registrarAcessoUseCase;

    @GetMapping("/atividade-vendas")
    public ResponseEntity<List<DiaVendaDTO>> getAtividadeVendas(@RequestParam(defaultValue = "2026") int ano) {
        List<DiaVendaDTO> dados = obterAtividadeGridUseCase.obterDadosGrid(ano);
        return ResponseEntity.ok(dados);
    }

    @GetMapping("/ultimas-vendas")
    public ResponseEntity<List<UltimaVendaDTO>> getUltimasVendas(@RequestParam(defaultValue = "10") int limite) {
        List<UltimaVendaDTO> ultimasVendas = obterUltimasVendasUseCase.executar(limite);
        return ResponseEntity.ok(ultimasVendas);
    }

    @GetMapping("/acessos-por-horario")
    public ResponseEntity<List<AcessoHorarioDTO>> getAcessosPorHorario(
            @RequestParam(value = "data", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        List<AcessoHorarioDTO> dados = obterAcessosPorHorarioUseCase.obterAcessosPorHorario(data);
        return ResponseEntity.ok(dados);
    }

    @PostMapping("/log-acesso")
    public ResponseEntity<Void> registrarAcesso(
            @RequestBody RegistrarLogAcessoDTO dto,
            HttpServletRequest request) {

        // Captura o IP real do cliente
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        } else {
            // Caso venha múltiplos IPs via Proxy/Nginx, pega o primeiro
            clientIp = clientIp.split(",")[0].trim();
        }

        dto.setIp(clientIp);

        registrarAcessoUseCase.executar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}