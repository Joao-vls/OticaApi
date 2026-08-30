package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarProdutosPromocaoUseCase {

    private final ProdutoRepository produtoRepository;
    private final ProdutoVarianteRepository varianteRepository;

    /**
     * Lista todos os produtos que possuem variantes em promoção
     * (quando priceOverride está definido e menor que o preço base do produto).
     */
    public List<Produto> listarEmPromocao() {
        // Buscar todas as variantes com preço promocional ativas e não deletadas
        List<ProdutoVariante> variantesPromocionais = varianteRepository.findAll().stream()
                .filter(v -> v.getPriceOverride() != null
                        && v.getProduto() != null
                        && v.getPriceOverride().compareTo(v.getProduto().getPreco()) < 0
                        && v.getDeletadoEm() == null
                        && Boolean.TRUE.equals(v.getAtivo())
                        && Boolean.TRUE.equals(v.getProduto().getAtivo())
                        && v.getProduto().getDeletadoEm() == null)
                .toList();

        // Extrair os produtos únicos dessas variantes
        return variantesPromocionais.stream()
                .map(ProdutoVariante::getProduto)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Lista produtos em promoção associados a uma categoria específica.
     */
    public List<Produto> listarPromocaoPorCategoria(UUID categoriaId) {
        return listarEmPromocao().stream()
                .filter(p -> p.getCategorias() != null && p.getCategorias().stream()
                        .anyMatch(c -> c.getId().equals(categoriaId)))
                .collect(Collectors.toList());
    }

    /**
     * Lista produtos em promoção associados a uma marca específica.
     */
    public List<Produto> listarPromocaoPorMarca(UUID marcaId) {
        return listarEmPromocao().stream()
                .filter(p -> p.getMarcaId() != null && p.getMarcaId().equals(marcaId))
                .collect(Collectors.toList());
    }
}