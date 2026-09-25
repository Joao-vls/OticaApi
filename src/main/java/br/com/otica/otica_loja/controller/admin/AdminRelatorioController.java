package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.UseCases.relatorios.GerarRelatorioFinanceiroUseCase;

import br.com.otica.otica_loja.dto.RelatorioFinanceiroVendasDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/admin/relatorios")
public class AdminRelatorioController {

    @Autowired
    private GerarRelatorioFinanceiroUseCase gerarRelatorioFinanceiroUseCase;

    /**
     * Retorna o relatório financeiro e de vendas num período específico.
     * Restrito a ADMIN e GERENTE (Atendentes não devem ver faturamento total).
     *
     * Exemplo de chamada:
     * GET /admin/relatorios/financeiro-vendas?dataInicio=2026-09-01T00:00:00-03:00&dataFim=2026-09-30T23:59:59-03:00&limiteTopProdutos=5
     */
    @GetMapping("/financeiro-vendas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<RelatorioFinanceiroVendasDTO> getRelatorioFinanceiro(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dataInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dataFim,

            @RequestParam(defaultValue = "10") int limiteTopProdutos) {

        // Se a data de fim não for enviada, assume o momento exato atual
        if (dataFim == null) {
            dataFim = OffsetDateTime.now();
        }

        // Se a data de início não for enviada, assume os últimos 30 dias
        if (dataInicio == null) {
            dataInicio = dataFim.minusDays(30);
        }

        // Validação de segurança: a data inicial não pode ser depois da final
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data de início não pode ser posterior à data de fim.");
        }

        RelatorioFinanceiroVendasDTO relatorio = gerarRelatorioFinanceiroUseCase.executar(
                dataInicio,
                dataFim,
                limiteTopProdutos
        );

        return ResponseEntity.ok(relatorio);
    }
}