package br.com.otica.otica_loja.UseCases.relatorios;

import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;

import br.com.otica.otica_loja.dto.RelatorioEstoqueDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GerarRelatorioEstoqueUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    public RelatorioEstoqueDTO executar(int limiteAlertas) {
        RelatorioEstoqueDTO relatorio = new RelatorioEstoqueDTO();

        // 1. Visão Geral do Catálogo
        relatorio.setTotalProdutosAtivos(produtoRepository.countByAtivoTrueAndDeletadoEmIsNull());
        relatorio.setTotalProdutosInativos(produtoRepository.countByAtivoFalseAndDeletadoEmIsNull());
        relatorio.setTotalVariantes(varianteRepository.contarVariantesAtivas());

        // 2. Quantidade e Patrimônio
        Long totalItens = varianteRepository.somarTotalItensEmEstoque();
        relatorio.setTotalItensEmEstoque(totalItens != null ? totalItens : 0);

        BigDecimal valorTotal = varianteRepository.calcularValorTotalEstoque();
        relatorio.setValorTotalEstoqueEstimado(valorTotal != null ? valorTotal : BigDecimal.ZERO);

        // 3. Alertas Numéricos
        relatorio.setProdutosSemEstoque(varianteRepository.contarVariantesSemEstoque());
        relatorio.setProdutosEstoqueBaixo(varianteRepository.contarVariantesEstoqueBaixo());

        // 4. Lista Detalhada de Alertas (para reposição de estoque)
        List<RelatorioEstoqueDTO.AlertaEstoqueDTO> alertas = varianteRepository
                .buscarItensAbaixoEstoqueMinimo(PageRequest.of(0, limiteAlertas))
                .stream()
                .map(proj -> new RelatorioEstoqueDTO.AlertaEstoqueDTO(
                        proj.getId(),
                        proj.getNomeProduto(),
                        proj.getSku(),
                        proj.getStock() != null ? proj.getStock() : 0,
                        proj.getEstoqueMinimo() != null ? proj.getEstoqueMinimo() : 0,
                        proj.getColorName() != null ? proj.getColorName() : "Padrão"
                ))
                .collect(Collectors.toList());

        relatorio.setItensAbaixoEstoqueMinimo(alertas);

        return relatorio;
    }
}