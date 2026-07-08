package br.com.otica.otica_loja.dto.dashboard;

import java.math.BigDecimal;

public class DashboardDTO {
    private long totalUsuarios;
    private long totalProdutos;
    private long produtosAtivos;
    private long produtosInativos;
    private long totalPagamentos;
    private long pagamentosAprovados;
    private long pagamentosPendentes;
    private long pagamentosRecusados;
    private BigDecimal receitaTotal;

    // Getters e Setters
    public long getTotalUsuarios() { return totalUsuarios; }
    public void setTotalUsuarios(long totalUsuarios) { this.totalUsuarios = totalUsuarios; }

    public long getTotalProdutos() { return totalProdutos; }
    public void setTotalProdutos(long totalProdutos) { this.totalProdutos = totalProdutos; }

    public long getProdutosAtivos() { return produtosAtivos; }
    public void setProdutosAtivos(long produtosAtivos) { this.produtosAtivos = produtosAtivos; }

    public long getProdutosInativos() { return produtosInativos; }
    public void setProdutosInativos(long produtosInativos) { this.produtosInativos = produtosInativos; }

    public long getTotalPagamentos() { return totalPagamentos; }
    public void setTotalPagamentos(long totalPagamentos) { this.totalPagamentos = totalPagamentos; }

    public long getPagamentosAprovados() { return pagamentosAprovados; }
    public void setPagamentosAprovados(long pagamentosAprovados) { this.pagamentosAprovados = pagamentosAprovados; }

    public long getPagamentosPendentes() { return pagamentosPendentes; }
    public void setPagamentosPendentes(long pagamentosPendentes) { this.pagamentosPendentes = pagamentosPendentes; }

    public long getPagamentosRecusados() { return pagamentosRecusados; }
    public void setPagamentosRecusados(long pagamentosRecusados) { this.pagamentosRecusados = pagamentosRecusados; }

    public BigDecimal getReceitaTotal() { return receitaTotal; }
    public void setReceitaTotal(BigDecimal receitaTotal) { this.receitaTotal = receitaTotal; }
}
