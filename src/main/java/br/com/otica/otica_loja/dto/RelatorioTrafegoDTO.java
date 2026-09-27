package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.dto.dashboard.AcessoHorarioDTO;
import lombok.Data;
import java.util.List;

@Data
public class RelatorioTrafegoDTO {

    // Visão Geral
    private long totalAcessos;
    private long visitantesUnicos;

    // Engajamento
    private double mediaAcessosPorVisitante;

    // Listas Detalhadas
    private List<RotaAcessoDTO> rotasMaisVisitadas;
    private List<AcessoHorarioDTO> acessosPorHorario; // Reaproveitamos o seu DTO existente!

    @Data
    public static class RotaAcessoDTO {
        private String rota;
        private long quantidadeAcessos;

        public RotaAcessoDTO(String rota, long quantidadeAcessos) {
            this.rota = rota;
            this.quantidadeAcessos = quantidadeAcessos;
        }
    }
}