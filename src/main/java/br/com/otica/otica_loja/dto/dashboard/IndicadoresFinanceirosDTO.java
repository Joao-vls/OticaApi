package br.com.otica.otica_loja.dto.dashboard;

import java.math.BigDecimal;

public class IndicadoresFinanceirosDTO {
    private BigDecimal receitaTotal;
    private BigDecimal lucroEstimado;
    private BigDecimal ticketMedio;
    private long pagamentosPendentes;
    private long pagamentosRecusados;

    // Getters e Setters
    public BigDecimal getReceitaTotal() { return receitaTotal; }
    public void setReceitaTotal(BigDecimal receitaTotal) { this.receitaTotal = receitaTotal; }

    public BigDecimal getLucroEstimado() { return lucroEstimado; }
    public void setLucroEstimado(BigDecimal lucroEstimado) { this.lucroEstimado = lucroEstimado; }

    public BigDecimal getTicketMedio() { return ticketMedio; }
    public void setTicketMedio(BigDecimal ticketMedio) { this.ticketMedio = ticketMedio; }

    public long getPagamentosPendentes() { return pagamentosPendentes; }
    public void setPagamentosPendentes(long pagamentosPendentes) { this.pagamentosPendentes = pagamentosPendentes; }

    public long getPagamentosRecusados() { return pagamentosRecusados; }
    public void setPagamentosRecusados(long pagamentosRecusados) { this.pagamentosRecusados = pagamentosRecusados; }
}
