package br.com.otica.otica_loja.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RelatorioEstoqueDTO {

    // Visão Geral do Catálogo
    private long totalProdutosAtivos;
    private long totalProdutosInativos;
    private long totalVariantes;

    // Visão Financeira e Quantitativa
    private long totalItensEmEstoque;
    private BigDecimal valorTotalEstoqueEstimado; // Soma de (Preço * EstoqueAtual)

    // Saúde e Alertas
    private long produtosSemEstoque; // SKUs zerados
    private long produtosEstoqueBaixo; // SKUs abaixo do limite mínimo

    // Listas de Alerta
    private List<AlertaEstoqueDTO> itensAbaixoEstoqueMinimo;

    @Data
    public static class AlertaEstoqueDTO {
        private String idVariante;
        private String nomeProduto;
        private String sku;
        private int saldoAtual;
        private int estoqueMinimo;
        private String cor;

        public AlertaEstoqueDTO(String idVariante, String nomeProduto, String sku, int saldoAtual, int estoqueMinimo, String cor) {
            this.idVariante = idVariante;
            this.nomeProduto = nomeProduto;
            this.sku = sku;
            this.saldoAtual = saldoAtual;
            this.estoqueMinimo = estoqueMinimo;
            this.cor = cor;
        }
    }
}