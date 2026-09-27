package br.com.otica.otica_loja.UseCases.relatorios;

import br.com.otica.otica_loja.Repository.CRM.CrmClienteRepository;
import br.com.otica.otica_loja.Repository.CRM.LeadRepository;

import br.com.otica.otica_loja.dto.RelatorioCrmRetencaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GerarRelatorioCrmUseCase {

    @Autowired
    private CrmClienteRepository crmClienteRepository;

    @Autowired
    private LeadRepository leadRepository;

    public RelatorioCrmRetencaoDTO executar() {
        RelatorioCrmRetencaoDTO relatorio = new RelatorioCrmRetencaoDTO();

        // 1. Base Geral e Leads
        long totalClientes = crmClienteRepository.count();
        long leadsNaoConvertidos = leadRepository.countByConvertidoFalse();
        long leadsConvertidos = leadRepository.countByConvertidoTrue();
        long totalLeads = leadsNaoConvertidos + leadsConvertidos;

        relatorio.setTotalClientesBase(totalClientes);
        relatorio.setTotalLeadsNaoConvertidos(leadsNaoConvertidos);

        if (totalLeads > 0) {
            double taxa = ((double) leadsConvertidos / totalLeads) * 100;
            // Arredonda para 2 casas decimais
            taxa = BigDecimal.valueOf(taxa).setScale(2, RoundingMode.HALF_UP).doubleValue();
            relatorio.setTaxaConversaoLeads(taxa);
        } else {
            relatorio.setTaxaConversaoLeads(0.0);
        }

        // 2. Saúde da Base (Churn)
        OffsetDateTime hoje = OffsetDateTime.now();
        OffsetDateTime seisMesesAtras = hoje.minusMonths(6);
        OffsetDateTime umAnoAtras = hoje.minusYears(1);

        // Comprou nos últimos 6 meses (Ativos)
        relatorio.setClientesAtivosRecentes(crmClienteRepository.contarAtivosRecentes(seisMesesAtras));

        // Não compra entre 6 a 12 meses (Risco de esquecer a loja / Receita vencendo)
        relatorio.setClientesEmRisco(crmClienteRepository.contarEmRisco(seisMesesAtras, umAnoAtras));

        // Não compra há mais de 1 ano ou nunca comprou (Inativos)
        relatorio.setClientesInativos(crmClienteRepository.contarInativos(umAnoAtras));

        // 3. Distribuição de Níveis (Curva ABC)
        List<RelatorioCrmRetencaoDTO.DistribuicaoNivelDTO> niveis = crmClienteRepository.agruparPorNivel()
                .stream()
                .map(proj -> new RelatorioCrmRetencaoDTO.DistribuicaoNivelDTO(
                        proj.getNivel().name(),
                        proj.getQuantidade()))
                .collect(Collectors.toList());
        relatorio.setDistribuicaoPorNivel(niveis);

        // 4. Engajamento pelo Score (Gamificação)
        relatorio.setClientesBaixoEngajamento(crmClienteRepository.contarPorFaixaDeScore(-1, 199)); // < 200
        relatorio.setClientesMedioEngajamento(crmClienteRepository.contarPorFaixaDeScore(199, 1000)); // 200 a 1000
        relatorio.setClientesAltoEngajamento(crmClienteRepository.contarPorScoreAcimaDe(1000)); // > 1000

        return relatorio;
    }
}