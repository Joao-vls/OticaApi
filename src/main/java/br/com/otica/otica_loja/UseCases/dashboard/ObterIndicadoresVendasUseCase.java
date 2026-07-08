package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Entity.Pedidos.Pagamento;
import br.com.otica.otica_loja.Repository.Pedidos.PagamentoRepository;
import br.com.otica.otica_loja.dto.dashboard.IndicadoresVendasDTO;
import br.com.otica.otica_loja.enums.StatusPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ObterIndicadoresVendasUseCase {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    /**
     * Retorna indicadores de vendas consolidados.
     */
    public IndicadoresVendasDTO obterIndicadores() {
        IndicadoresVendasDTO dto = new IndicadoresVendasDTO();

        // Buscar todos os pagamentos aprovados
        List<Pagamento> pagamentosAprovados = pagamentoRepository.findByStatus(StatusPagamento.APROVADO);

        // Receita total
        BigDecimal receitaTotal = pagamentosAprovados.stream()
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setReceitaTotal(receitaTotal);

        // Quantidade de vendas
        long totalVendas = pagamentosAprovados.size();
        dto.setTotalVendas(totalVendas);

        // Ticket médio
        BigDecimal ticketMedio = totalVendas > 0
                ? receitaTotal.divide(BigDecimal.valueOf(totalVendas), BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        dto.setTicketMedio(ticketMedio);

        // Pagamentos por status
        dto.setPagamentosPendentes(pagamentoRepository.findByStatus(StatusPagamento.PENDENTE).size());
        dto.setPagamentosRecusados(pagamentoRepository.findByStatus(StatusPagamento.RECUSADO).size());
        dto.setPagamentosAprovados(totalVendas);

        return dto;
    }
}
