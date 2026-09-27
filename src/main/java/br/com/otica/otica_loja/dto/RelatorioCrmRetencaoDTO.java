package br.com.otica.otica_loja.dto;

import lombok.Data;
import java.util.List;

@Data
public class RelatorioCrmRetencaoDTO {

    // Métricas Globais
    private long totalClientesBase;
    private long totalLeadsNaoConvertidos;
    private double taxaConversaoLeads; // Porcentagem

    // Retenção / Risco de Churn (Evasão)
    private long clientesAtivosRecentes; // Compraram nos últimos 6 meses
    private long clientesEmRisco; // Não compram há mais de 6 meses
    private long clientesInativos; // Não compram há mais de 1 ano

    // Distribuição de Níveis
    private List<DistribuicaoNivelDTO> distribuicaoPorNivel;

    // Engajamento
    private long clientesAltoEngajamento; // Score > 1000
    private long clientesMedioEngajamento; // Score entre 200 e 1000
    private long clientesBaixoEngajamento; // Score < 200

    @Data
    public static class DistribuicaoNivelDTO {
        private String nivel;
        private long quantidade;

        public DistribuicaoNivelDTO(String nivel, long quantidade) {
            this.nivel = nivel;
            this.quantidade = quantidade;
        }
    }
}