package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Entity.Pedidos.Pagamento;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PagamentoRepository;
import br.com.otica.otica_loja.dto.dashboard.DashboardDTO;
import br.com.otica.otica_loja.enums.StatusPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ObterDashboardUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    /**
     * Retorna um objeto com métricas gerais da loja.
     */
    public DashboardDTO obterDashboard() {
        DashboardDTO dto = new DashboardDTO();

        // Usuários
        dto.setTotalUsuarios(usuarioRepository.count());

        // Produtos
        dto.setTotalProdutos(produtoRepository.count());
        dto.setProdutosAtivos(produtoRepository.findByAtivoTrue().size());
        dto.setProdutosInativos(produtoRepository.findByAtivoFalse().size());

        // Pagamentos
        dto.setTotalPagamentos(pagamentoRepository.count());
        dto.setPagamentosAprovados(pagamentoRepository.findByStatus(StatusPagamento.APROVADO).size());
        dto.setPagamentosPendentes(pagamentoRepository.findByStatus(StatusPagamento.PENDENTE).size());
        dto.setPagamentosRecusados(pagamentoRepository.findByStatus(StatusPagamento.RECUSADO).size());

        // Receita total
        BigDecimal receitaTotal = pagamentoRepository.findByStatus(StatusPagamento.APROVADO)
                .stream()
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setReceitaTotal(receitaTotal);

        return dto;
    }
}
