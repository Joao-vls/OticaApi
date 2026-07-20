package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Repository.Pedidos.PagamentoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PagamentoRepository.MetricasStatusProjection;
import br.com.otica.otica_loja.dto.dashboard.IndicadoresVendasDTO;
import br.com.otica.otica_loja.enums.StatusPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ObterIndicadoresVendasUseCase {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    /**
     * Retorna indicadores de vendas consolidados de forma otimizada.
     */
    public IndicadoresVendasDTO obterIndicadores() {
        IndicadoresVendasDTO dto = new IndicadoresVendasDTO();

        // Uma única query agrupada traz tudo o que precisamos do banco
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
                totalVendasAprovadas = quantidade;
                receitaTotal = somaValor;
            } else if (status == StatusPagamento.PENDENTE) {
                pendentes = quantidade;
            } else if (status == StatusPagamento.RECUSADO) {
                recusados = quantidade;
            }
        }

        // Popula o DTO com os valores consolidados
        dto.setReceitaTotal(receitaTotal);
        dto.setTotalVendas(totalVendasAprovadas);
        dto.setPagamentosAprovados(totalVendasAprovadas);
        dto.setPagamentosPendentes((int) pendentes);
        dto.setPagamentosRecusados((int) recusados);

        // Ticket médio calculado com RoundingMode seguro (evita o aviso de depreciação do Java)
        BigDecimal ticketMedio = totalVendasAprovadas > 0
                ? receitaTotal.divide(BigDecimal.valueOf(totalVendasAprovadas), RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        dto.setTicketMedio(ticketMedio);

        return dto;
    }
}