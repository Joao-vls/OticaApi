package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.UseCases.dashboard.ObterAtividadeGridUseCase;
import br.com.otica.otica_loja.dto.dashboard.DiaVendaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    @Autowired
    private ObterAtividadeGridUseCase obterAtividadeGridUseCase;

    @GetMapping("/atividade-vendas")
    public ResponseEntity<List<DiaVendaDTO>> getAtividadeVendas(@RequestParam(defaultValue = "2026") int ano) {
        // Agora recebe apenas a lista limpa e otimizada de dias com vendas
        List<DiaVendaDTO> dados = obterAtividadeGridUseCase.obterDadosGrid(ano);
        return ResponseEntity.ok(dados);
    }
}