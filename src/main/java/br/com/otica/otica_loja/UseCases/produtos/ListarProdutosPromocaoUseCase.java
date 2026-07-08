package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListarProdutosPromocaoUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    /**
     * Lista todos os produtos que possuem variantes em promoção
     * (quando priceOverride está definido e menor que o preço base).
     */
    public List<Produto> listarEmPromocao() {
        // Buscar todas as variantes com preço promocional
        List<ProdutoVariante> variantesPromocionais = varianteRepository.findAll().stream()
                .filter(v -> v.getPriceOverride() != null
                        && v.getPriceOverride().compareTo(v.getProduto().getPreco()) < 0
                        && v.getDeletadoEm() == null
                        && Boolean.TRUE.equals(v.getAtivo()))
                .toList();

        // Extrair os produtos dessas variantes
        return variantesPromocionais.stream()
                .map(ProdutoVariante::getProduto)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Lista produtos em promoção dentro de uma categoria específica.
     */
    public List<Produto> listarPromocaoPorCategoria(UUID categoriaId) {
        return listarEmPromocao().stream()
                .filter(p -> p.getCategoriaId().equals(categoriaId))
                .collect(Collectors.toList());
    }

    /**
     * Lista produtos em promoção de uma marca específica.
     */
    public List<Produto> listarPromocaoPorMarca(UUID marcaId) {
        return listarEmPromocao().stream()
                .filter(p -> p.getMarcaId().equals(marcaId))
                .collect(Collectors.toList());
    }
}
