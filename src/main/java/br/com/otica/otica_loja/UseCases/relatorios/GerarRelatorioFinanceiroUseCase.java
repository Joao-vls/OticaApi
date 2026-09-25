package br.com.otica.otica_loja.UseCases.relatorios;

import br.com.otica.otica_loja.Repository.Pedidos.PagamentoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.dto.RelatorioFinanceiroVendasDTO;
import br.com.otica.otica_loja.enums.StatusPagamento;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GerarRelatorioFinanceiroUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    public RelatorioFinanceiroVendasDTO executar(OffsetDateTime dataInicio, OffsetDateTime dataFim, int limiteTopProdutos) {
        RelatorioFinanceiroVendasDTO relatorio = new RelatorioFinanceiroVendasDTO();

        // 1. Consideramos como "Venda Realizada" pedidos que foram pagos para frente
        List<StatusPedido> statusValidosVenda = Arrays.asList(
                StatusPedido.PAGO,
                StatusPedido.SEPARACAO,
                StatusPedido.ENVIADO,
                StatusPedido.ENTREGUE
        );

        // 2. Obter Resumo Financeiro
        PedidoRepository.ResumoFinanceiroProjection resumo = pedidoRepository
                .obterResumoFinanceiroNoPeriodo(statusValidosVenda, dataInicio, dataFim);

        long qtdPedidos = (resumo != null && resumo.getQuantidade() != null) ? resumo.getQuantidade() : 0;
        BigDecimal receitaTotal = (resumo != null && resumo.getReceitaTotal() != null) ? resumo.getReceitaTotal() : BigDecimal.ZERO;
        BigDecimal descontos = (resumo != null && resumo.getTotalDescontos() != null) ? resumo.getTotalDescontos() : BigDecimal.ZERO;
        BigDecimal fretes = (resumo != null && resumo.getTotalFrete() != null) ? resumo.getTotalFrete() : BigDecimal.ZERO;

        relatorio.setQuantidadePedidosPagos(qtdPedidos);
        relatorio.setReceitaTotal(receitaTotal);
        relatorio.setTotalDescontosConcedidos(descontos);
        relatorio.setTotalFreteCobrado(fretes);

        // Calcular Ticket Médio
        if (qtdPedidos > 0) {
            relatorio.setTicketMedio(receitaTotal.divide(BigDecimal.valueOf(qtdPedidos), 2, RoundingMode.HALF_UP));
        } else {
            relatorio.setTicketMedio(BigDecimal.ZERO);
        }

        // 3. Obter Agrupamento por Métodos de Pagamento
        List<RelatorioFinanceiroVendasDTO.VendasPorMetodoDTO> metodosDto = pagamentoRepository
                .agruparPorMetodoPagamento(StatusPagamento.APROVADO, dataInicio, dataFim)
                .stream()
                .map(proj -> new RelatorioFinanceiroVendasDTO.VendasPorMetodoDTO(
                        proj.getMetodo(),
                        proj.getQuantidade(),
                        proj.getTotal()))
                .collect(Collectors.toList());
        relatorio.setVendasPorMetodo(metodosDto);

        // 4. Obter Top Produtos
        List<RelatorioFinanceiroVendasDTO.TopProdutoDTO> topProdutosDto = pedidoItemRepository
                .obterProdutosMaisVendidos(statusValidosVenda, dataInicio, dataFim, PageRequest.of(0, limiteTopProdutos))
                .stream()
                .map(proj -> new RelatorioFinanceiroVendasDTO.TopProdutoDTO(
                        proj.getNomeProduto(),
                        proj.getQuantidadeVendida(),
                        proj.getReceitaGerada()))
                .collect(Collectors.toList());
        relatorio.setProdutosMaisVendidos(topProdutosDto);

        return relatorio;
    }
}