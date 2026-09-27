package br.com.otica.otica_loja.UseCases.relatorios;

import br.com.otica.otica_loja.Repository.Admin.LogAcessoRepository;
import br.com.otica.otica_loja.dto.RelatorioTrafegoDTO;
import br.com.otica.otica_loja.dto.dashboard.AcessoHorarioDTO;
import br.com.otica.otica_loja.dto.dashboard.AcessoHorarioProjection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GerarRelatorioTrafegoUseCase {

    @Autowired
    private LogAcessoRepository logAcessoRepository;

    public RelatorioTrafegoDTO executar(OffsetDateTime dataInicio, OffsetDateTime dataFim, int limiteRotas) {
        RelatorioTrafegoDTO relatorio = new RelatorioTrafegoDTO();

        // 1. Visão Geral
        long totalAcessos = logAcessoRepository.contarAcessosNoPeriodo(dataInicio, dataFim);
        long visitantesUnicos = logAcessoRepository.contarVisitantesUnicosNoPeriodo(dataInicio, dataFim);

        relatorio.setTotalAcessos(totalAcessos);
        relatorio.setVisitantesUnicos(visitantesUnicos);

        if (visitantesUnicos > 0) {
            double media = (double) totalAcessos / visitantesUnicos;
            relatorio.setMediaAcessosPorVisitante(BigDecimal.valueOf(media).setScale(2, RoundingMode.HALF_UP).doubleValue());
        } else {
            relatorio.setMediaAcessosPorVisitante(0.0);
        }

        // 2. Top Rotas Visitadas (Ex: /produtos/oculos-rayban)
        List<RelatorioTrafegoDTO.RotaAcessoDTO> rotas = logAcessoRepository
                .buscarTopRotas(dataInicio, dataFim, PageRequest.of(0, limiteRotas))
                .stream()
                .map(proj -> new RelatorioTrafegoDTO.RotaAcessoDTO(proj.getRota(), proj.getQuantidade()))
                .collect(Collectors.toList());
        relatorio.setRotasMaisVisitadas(rotas);

        // 3. Tráfego por Horário (Usa a sua query nativa já existente!)
        List<AcessoHorarioProjection> acessosBanco = logAcessoRepository.buscarAcessosPorHorario(dataInicio, dataFim);

        Map<String, Long> mapaAcessos = acessosBanco.stream().collect(Collectors.toMap(
                AcessoHorarioProjection::getHorario, AcessoHorarioProjection::getQuantidade, (v1, v2) -> v1
        ));

        List<AcessoHorarioDTO> listaHorarios = new ArrayList<>();
        for (int hora = 0; hora < 24; hora++) {
            String labelHorario = formatarIntervaloHora(hora);
            listaHorarios.add(new AcessoHorarioDTO(labelHorario, mapaAcessos.getOrDefault(labelHorario, 0L)));
        }
        relatorio.setAcessosPorHorario(listaHorarios);

        return relatorio;
    }

    private String formatarIntervaloHora(int hora) {
        return String.format("%02d:00-%02d:00", hora, (hora + 1) % 24);
    }
}