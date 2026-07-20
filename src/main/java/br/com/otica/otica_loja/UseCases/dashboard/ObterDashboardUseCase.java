package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PagamentoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PagamentoRepository.MetricasStatusProjection;
import br.com.otica.otica_loja.dto.dashboard.DashboardDTO;
import br.com.otica.otica_loja.enums.StatusPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ObterDashboardUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    public DashboardDTO obterDashboard() {
        DashboardDTO dto = new DashboardDTO();

        // 1. Usuários e Produtos (Consultas via COUNT direto no banco)
        dto.setTotalUsuarios(usuarioRepository.count());
        dto.setTotalProdutos(produtoRepository.count());
        dto.setProdutosAtivos((int) produtoRepository.countByAtivoTrue());
        dto.setProdutosInativos((int) produtoRepository.countByAtivoFalse());

        // 2. Pagamentos (Uma única query agregada para obter todas as métricas)
        List<MetricasStatusProjection> metricas = pagamentoRepository.obterMetricasAgrupadasPorStatus();

        long totalPagamentos = 0;
        long aprovados = 0;
        long pendentes = 0;
        long recusados = 0;
        BigDecimal receitaTotal = BigDecimal.ZERO;

        for (MetricasStatusProjection registro : metricas) {
            StatusPagamento status = registro.getStatus();
            long quantidade = registro.getQuantidade();
            BigDecimal somaValor = registro.getSomaValor() != null ? registro.getSomaValor() : BigDecimal.ZERO;

            totalPagamentos += quantidade;

            if (status == StatusPagamento.APROVADO) {
                aprovados = quantidade;
                receitaTotal = somaValor;
            } else if (status == StatusPagamento.PENDENTE) {
                pendentes = quantidade;
            } else if (status == StatusPagamento.RECUSADO) {
                recusados = quantidade;
            }
        }

        // Atribuindo os resultados consolidados ao DTO
        dto.setTotalPagamentos(totalPagamentos);
        dto.setPagamentosAprovados(aprovados);
        dto.setPagamentosPendentes(pendentes);
        dto.setPagamentosRecusados(recusados);
        dto.setReceitaTotal(receitaTotal);

        return dto;
    }
}