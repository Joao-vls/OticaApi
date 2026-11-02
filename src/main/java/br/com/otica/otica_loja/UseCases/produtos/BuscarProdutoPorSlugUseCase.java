package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;

import br.com.otica.otica_loja.dto.ProdutoDetalheDTO;
import br.com.otica.otica_loja.mapper.ProdutoDetalheMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarProdutoPorSlugUseCase {

    private final ProdutoRepository produtoRepository;
    private final ProdutoDetalheMapper produtoDetalheMapper;

    @Cacheable(value = "produto", key = "'slug:' + #p0")
    @Transactional(readOnly = true)
    public ProdutoDetalheDTO executar(String slug) {

        Produto produto = produtoRepository.findBySlug(slug)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado com o slug: " + slug
                        )
                );

        return produtoDetalheMapper.toDTO(produto);
    }
}