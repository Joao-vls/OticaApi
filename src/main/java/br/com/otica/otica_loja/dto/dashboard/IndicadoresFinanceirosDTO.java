package br.com.otica.otica_loja.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class IndicadoresFinanceirosDTO {
    // Getters e Setters
    private BigDecimal receitaTotal;
    private BigDecimal lucroEstimado;
    private BigDecimal ticketMedio;
    private long pagamentosPendentes;
    private long pagamentosRecusados;

}
