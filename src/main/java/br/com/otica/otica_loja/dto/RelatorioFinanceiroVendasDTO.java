package br.com.otica.otica_loja.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RelatorioFinanceiroVendasDTO {
    private BigDecimal receitaTotal;
    private long quantidadePedidosPagos;
    private BigDecimal ticketMedio;
    private BigDecimal totalDescontosConcedidos;
    private BigDecimal totalFreteCobrado;

    // Listas detalhadas
    private List<VendasPorMetodoDTO> vendasPorMetodo;
    private List<TopProdutoDTO> produtosMaisVendidos;

    @Data
    public static class VendasPorMetodoDTO {
        private String metodo;
        private long quantidade;
        private BigDecimal total;

        public VendasPorMetodoDTO(String metodo, long quantidade, BigDecimal total) {
            this.metodo = metodo;
            this.quantidade = quantidade;
            this.total = total;
        }
    }

    @Data
    public static class TopProdutoDTO {
        private String nomeProduto;
        private long quantidadeVendida;
        private BigDecimal receitaGerada;

        public TopProdutoDTO(String nomeProduto, long quantidadeVendida, BigDecimal receitaGerada) {
            this.nomeProduto = nomeProduto;
            this.quantidadeVendida = quantidadeVendida;
            this.receitaGerada = receitaGerada;
        }
    }
}