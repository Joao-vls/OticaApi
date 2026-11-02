package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;

import br.com.otica.otica_loja.dto.ProdutoDetalheDTO;
import br.com.otica.otica_loja.mapper.ProdutoDetalheMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarProdutoPorIdUseCase {

    private final ProdutoRepository produtoRepository;
    private final ProdutoDetalheMapper produtoDetalheMapper;

    @Cacheable(value = "produto", key = "'id:' + #p0")
    @Transactional(readOnly = true)
    public ProdutoDetalheDTO buscarPorId(UUID produtoId) {

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Produto não encontrado.")
                );

        return produtoDetalheMapper.toDTO(produto);
    }
}