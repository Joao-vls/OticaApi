package br.com.otica.otica_loja.dto.dashboard;

public class IndicadoresProdutosDTO {
    private long totalProdutos;
    private long produtosAtivos;
    private long produtosInativos;
    private long produtosDestaque;
    private long produtosDeletados;
    private long produtosNaoDeletados;

    // Getters e Setters
    public long getTotalProdutos() { return totalProdutos; }
    public void setTotalProdutos(long totalProdutos) { this.totalProdutos = totalProdutos; }

    public long getProdutosAtivos() { return produtosAtivos; }
    public void setProdutosAtivos(long produtosAtivos) { this.produtosAtivos = produtosAtivos; }

    public long getProdutosInativos() { return produtosInativos; }
    public void setProdutosInativos(long produtosInativos) { this.produtosInativos = produtosInativos; }

    public long getProdutosDestaque() { return produtosDestaque; }
    public void setProdutosDestaque(long produtosDestaque) { this.produtosDestaque = produtosDestaque; }

    public long getProdutosDeletados() { return produtosDeletados; }
    public void setProdutosDeletados(long produtosDeletados) { this.produtosDeletados = produtosDeletados; }

    public long getProdutosNaoDeletados() { return produtosNaoDeletados; }
    public void setProdutosNaoDeletados(long produtosNaoDeletados) { this.produtosNaoDeletados = produtosNaoDeletados; }
}
