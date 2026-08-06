package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Repository.Admin.LogAcessoRepository;
import br.com.otica.otica_loja.dto.dashboard.AcessoHorarioDTO;
import br.com.otica.otica_loja.dto.dashboard.AcessoHorarioProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ObterAcessosPorHorarioUseCase {

    @Autowired
    private LogAcessoRepository logAcessoRepository;

    private static final ZoneId ZONE_BRASIL = ZoneId.of("America/Sao_Paulo");

    public List<AcessoHorarioDTO> obterAcessosPorHorario(LocalDate data) {
        LocalDate dataConsulta = (data != null) ? data : LocalDate.now(ZONE_BRASIL);

        // Define o início e fim do dia considerando o fuso de Brasília (UTC-3)
        ZonedDateTime inicioDiaBrasil = dataConsulta.atStartOfDay(ZONE_BRASIL);
        ZonedDateTime fimDiaBrasil = dataConsulta.atTime(LocalTime.MAX).atZone(ZONE_BRASIL);

        // Converte para OffsetDateTime para bater com o tipo do repositório
        OffsetDateTime inicio = inicioDiaBrasil.toOffsetDateTime();
        OffsetDateTime fim = fimDiaBrasil.toOffsetDateTime();

        // Busca os dados agrupados no repositório usando a projeção
        List<AcessoHorarioProjection> acessosBanco = logAcessoRepository.buscarAcessosPorHorario(inicio, fim);

        // Mapeia usando os métodos getHorario() e getQuantidade() da interface AcessoHorarioProjection
        Map<String, Long> mapaAcessos = acessosBanco.stream()
                .collect(Collectors.toMap(
                        AcessoHorarioProjection::getHorario,
                        AcessoHorarioProjection::getQuantidade,
                        (v1, v2) -> v1
                ));

        // Monta a lista com as 24 horas do dia, preenchendo com 0 onde não houve acessos
        List<AcessoHorarioDTO> listaCompleta = new ArrayList<>();
        for (int hora = 0; hora < 24; hora++) {
            String labelHorario = formatarIntervaloHora(hora);
            Long quantidadeAcessos = mapaAcessos.getOrDefault(labelHorario, 0L);

            // Assume que seu AcessoHorarioDTO aceita (String, Long) no construtor
            listaCompleta.add(new AcessoHorarioDTO(labelHorario, quantidadeAcessos));
        }

        return listaCompleta;
    }

    private String formatarIntervaloHora(int hora) {
        String horaInicio = String.format("%02d:00", hora);
        String horaFim = String.format("%02d:00", (hora + 1) % 24);
        return horaInicio + "-" + horaFim;
    }
}