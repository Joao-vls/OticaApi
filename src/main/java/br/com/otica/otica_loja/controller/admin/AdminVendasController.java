package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.UseCases.dashboard.ObterDashboardUseCase;
import br.com.otica.otica_loja.UseCases.dashboard.ObterIndicadoresFinanceirosUseCase;
import br.com.otica.otica_loja.UseCases.dashboard.ObterIndicadoresVendasUseCase;
import br.com.otica.otica_loja.dto.dashboard.DashboardDTO;
import br.com.otica.otica_loja.dto.dashboard.IndicadoresFinanceirosDTO;
import br.com.otica.otica_loja.dto.dashboard.IndicadoresVendasDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/vendas")
@RequiredArgsConstructor
public class AdminVendasController {

    private final ObterDashboardUseCase obterDashboardUseCase;
    private final ObterIndicadoresVendasUseCase obterIndicadoresVendasUseCase;
    private final ObterIndicadoresFinanceirosUseCase obterIndicadoresFinanceirosUseCase;

    /**
     * Retorna o painel geral com métricas resumidas de usuários, produtos e pagamentos.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> obterDashboardGeral() {
        DashboardDTO dashboard = obterDashboardUseCase.obterDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Retorna os indicadores consolidados focados em performance de vendas (tickets, totais).
     */
    @GetMapping("/indicadores/vendas")
    public ResponseEntity<IndicadoresVendasDTO> obterIndicadoresVendas() {
        IndicadoresVendasDTO indicadores = obterIndicadoresVendasUseCase.obterIndicadores();
        return ResponseEntity.ok(indicadores);
    }

    /**
     * Retorna os indicadores focados na saúde financeira da loja (lucro estimado, receitas, custos).
     */
    @GetMapping("/indicadores/financeiros")
    public ResponseEntity<IndicadoresFinanceirosDTO> obterIndicadoresFinanceiros() {
        IndicadoresFinanceirosDTO indicadores = obterIndicadoresFinanceirosUseCase.obterIndicadores();
        return ResponseEntity.ok(indicadores);
    }
}