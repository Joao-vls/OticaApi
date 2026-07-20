package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Repository.Pedidos.PagamentoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PagamentoRepository.MetricasStatusProjection;
import br.com.otica.otica_loja.dto.dashboard.IndicadoresFinanceirosDTO;
import br.com.otica.otica_loja.enums.StatusPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ObterIndicadoresFinanceirosUseCase {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    /**
     * Retorna indicadores financeiros consolidados de forma otimizada.
     */
    public IndicadoresFinanceirosDTO obterIndicadores() {
        IndicadoresFinanceirosDTO dto = new IndicadoresFinanceirosDTO();

        // Agrega e conta tudo diretamente no banco de dados
        List<MetricasStatusProjection> metricas = pagamentoRepository.obterMetricasAgrupadasPorStatus();

        BigDecimal receitaTotal = BigDecimal.ZERO;
        long totalVendasAprovadas = 0;
        long pendentes = 0;
        long recusados = 0;

        for (MetricasStatusProjection registro : metricas) {
            StatusPagamento status = registro.getStatus();
            long quantidade = registro.getQuantidade();
            BigDecimal somaValor = registro.getSomaValor() != null ? registro.getSomaValor() : BigDecimal.ZERO;

            if (status == StatusPagamento.APROVADO) {
                receitaTotal = somaValor;
                totalVendasAprovadas = quantidade;
            } else if (status == StatusPagamento.PENDENTE) {
                pendentes = quantidade;
            } else if (status == StatusPagamento.RECUSADO) {
                recusados = quantidade; // Usando a variável correta em português
            }
        }

        // Popula o DTO com os valores agregados
        dto.setReceitaTotal(receitaTotal);
        dto.setPagamentosPendentes((int) pendentes);
        dto.setPagamentosRecusados((int) recusados);

        // Lucro estimado (exemplo: receita - 20% custos operacionais)
        BigDecimal custosEstimados = receitaTotal.multiply(BigDecimal.valueOf(0.20));
        dto.setLucroEstimado(receitaTotal.subtract(custosEstimados));

        // Ticket médio (Tratando divisão por zero e usando o RoundingMode moderno do Java)
        BigDecimal ticketMedio = totalVendasAprovadas > 0
                ? receitaTotal.divide(BigDecimal.valueOf(totalVendasAprovadas), RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        dto.setTicketMedio(ticketMedio);

        return dto;
    }
}