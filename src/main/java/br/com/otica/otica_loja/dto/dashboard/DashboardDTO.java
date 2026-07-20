package br.com.otica.otica_loja.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class DashboardDTO {
    // Getters e Setters
    private long totalUsuarios;
    private long totalProdutos;
    private long produtosAtivos;
    private long produtosInativos;
    private long totalPagamentos;
    private long pagamentosAprovados;
    private long pagamentosPendentes;
    private long pagamentosRecusados;
    private BigDecimal receitaTotal;

}
