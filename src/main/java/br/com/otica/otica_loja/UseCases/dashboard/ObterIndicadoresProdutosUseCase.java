package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.dto.dashboard.IndicadoresProdutosDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObterIndicadoresProdutosUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Retorna indicadores consolidados sobre os produtos.
     */
    public IndicadoresProdutosDTO obterIndicadores() {
        IndicadoresProdutosDTO dto = new IndicadoresProdutosDTO();

        // Total de produtos
        dto.setTotalProdutos(produtoRepository.count());

        // Produtos ativos e inativos
        dto.setProdutosAtivos(produtoRepository.findByAtivoTrue().size());
        dto.setProdutosInativos(produtoRepository.findByAtivoFalse().size());

        // Produtos em destaque
        dto.setProdutosDestaque(produtoRepository.findByDestaqueTrue().size());

        // Produtos deletados e não deletados (soft delete)
        dto.setProdutosDeletados(produtoRepository.findByDeletadoEmIsNotNull().size());
        dto.setProdutosNaoDeletados(produtoRepository.findByDeletadoEmIsNull().size());

        return dto;
    }
}
