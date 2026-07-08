package br.com.otica.otica_loja.dto.dashboard;

import java.math.BigDecimal;

public class IndicadoresVendasDTO {
    private BigDecimal receitaTotal;
    private long totalVendas;
    private BigDecimal ticketMedio;
    private long pagamentosPendentes;
    private long pagamentosRecusados;
    private long pagamentosAprovados;

    // Getters e Setters
    public BigDecimal getReceitaTotal() { return receitaTotal; }
    public void setReceitaTotal(BigDecimal receitaTotal) { this.receitaTotal = receitaTotal; }

    public long getTotalVendas() { return totalVendas; }
    public void setTotalVendas(long totalVendas) { this.totalVendas = totalVendas; }

    public BigDecimal getTicketMedio() { return ticketMedio; }
    public void setTicketMedio(BigDecimal ticketMedio) { this.ticketMedio = ticketMedio; }

    public long getPagamentosPendentes() { return pagamentosPendentes; }
    public void setPagamentosPendentes(long pagamentosPendentes) { this.pagamentosPendentes = pagamentosPendentes; }

    public long getPagamentosRecusados() { return pagamentosRecusados; }
    public void setPagamentosRecusados(long pagamentosRecusados) { this.pagamentosRecusados = pagamentosRecusados; }

    public long getPagamentosAprovados() { return pagamentosAprovados; }
    public void setPagamentosAprovados(long pagamentosAprovados) { this.pagamentosAprovados = pagamentosAprovados; }
}
