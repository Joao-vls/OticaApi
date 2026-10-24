package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.CRM.AssinaturaClube;
import br.com.otica.otica_loja.Entity.CRM.HistoricoBeneficio;
import br.com.otica.otica_loja.Repository.CRM.AssinaturaClubeRepository;
import br.com.otica.otica_loja.Repository.CRM.HistoricoBeneficioRepository;
import br.com.otica.otica_loja.UseCases.membros.AtivarAssinaturaAdminUseCase;
import br.com.otica.otica_loja.UseCases.membros.AtualizarAssinaturasVencidasJob;
import br.com.otica.otica_loja.UseCases.membros.LancarBeneficioMembroUseCase;
import br.com.otica.otica_loja.UseCases.membros.ListarRenovacoesPendentesUseCase;
import br.com.otica.otica_loja.dto.AtivarAssinaturaAdminRequestDTO;
import br.com.otica.otica_loja.dto.LancarBeneficioRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/membros")
@RequiredArgsConstructor
public class AdminMembrosController {

    private final AtivarAssinaturaAdminUseCase ativarAssinaturaAdminUseCase;
    private final LancarBeneficioMembroUseCase lancarBeneficioMembroUseCase;
    private final ListarRenovacoesPendentesUseCase listarRenovacoesPendentesUseCase;
    private final AssinaturaClubeRepository assinaturaClubeRepository;
    private final HistoricoBeneficioRepository historicoBeneficioRepository;
    private final AtualizarAssinaturasVencidasJob atualizarAssinaturasVencidasJob;
    // 1. Busca os detalhes da assinatura de um cliente
    @GetMapping("/{usuarioId}")
    public ResponseEntity<AssinaturaClube> buscarAssinaturaPorUsuario(@PathVariable UUID usuarioId) {
        return assinaturaClubeRepository.findByUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 2. O Admin recebe o PIX e ativa/renova a assinatura pelo painel
    @PostMapping("/{usuarioId}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<AssinaturaClube> ativarAssinatura(
            @PathVariable UUID usuarioId,
            @Valid @RequestBody AtivarAssinaturaAdminRequestDTO dto) {

        AssinaturaClube assinatura = ativarAssinaturaAdminUseCase.executar(usuarioId, dto);
        return ResponseEntity.ok(assinatura);
    }

    // 3. O Admin lança que o membro usou uma troca anual ou ganhou um brinde
    @PostMapping("/{usuarioId}/beneficios")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<?> lancarBeneficio(
            @PathVariable UUID usuarioId,
            @Valid @RequestBody LancarBeneficioRequestDTO dto) {

        try {
            HistoricoBeneficio beneficio = lancarBeneficioMembroUseCase.executar(usuarioId, dto);
            return ResponseEntity.ok(beneficio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. Lista o histórico de benefícios utilizados por esse cliente
    @GetMapping("/{usuarioId}/beneficios")
    public ResponseEntity<List<HistoricoBeneficio>> listarHistoricoBeneficios(@PathVariable UUID usuarioId) {
        // Busca a assinatura primeiro
        AssinaturaClube assinatura = assinaturaClubeRepository.findByUsuarioId(usuarioId).orElse(null);
        if (assinatura == null) {
            return ResponseEntity.notFound().build();
        }

        List<HistoricoBeneficio> historico = historicoBeneficioRepository
                .findByAssinaturaIdOrderByDataUtilizacaoDesc(assinatura.getId());
        return ResponseEntity.ok(historico);
    }

    // 5. Dashboard: Lista membros cuja assinatura vence nos próximos X dias (ex: 15 dias)
    @GetMapping("/renovacoes/proximas")
    public ResponseEntity<List<AssinaturaClube>> listarRenovacoesProximas(@RequestParam(defaultValue = "15") int dias) {
        List<AssinaturaClube> assinaturas = listarRenovacoesPendentesUseCase.listarVencendoNosProximosDias(dias);
        return ResponseEntity.ok(assinaturas);
    }

    // 6. Dashboard: Lista membros com assinatura vencida (passou da data, mas consta como ATIVA ainda)
    @GetMapping("/renovacoes/atrasadas")
    public ResponseEntity<List<AssinaturaClube>> listarRenovacoesAtrasadas() {
        List<AssinaturaClube> assinaturas = listarRenovacoesPendentesUseCase.listarVencidasPendentes();
        return ResponseEntity.ok(assinaturas);
    }
    @PostMapping("/rotinas/forcar-vencimentos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> forcarAtualizacaoVencidos() {
        try {
            atualizarAssinaturasVencidasJob.verificarEAtualizarVencidas();
            return ResponseEntity.ok().body("Rotina executada com sucesso. Verifique os logs do servidor para detalhes.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao executar rotina: " + e.getMessage());
        }
    }
}