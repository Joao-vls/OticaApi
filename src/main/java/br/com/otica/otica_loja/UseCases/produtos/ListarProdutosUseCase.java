package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.dto.ProdutoDetalheDTO;
import br.com.otica.otica_loja.mapper.ProdutoDetalheMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarProdutosUseCase {



    private final ProdutoRepository produtoRepository;
    private final ProdutoDetalheMapper produtoDetalheMapper;

    @Cacheable(value = "catalogo", key = "'todos_ativos'")
    @Transactional(readOnly = true)
    public List<ProdutoDetalheDTO> listarAtivos() {
        return produtoRepository.findByAtivoTrue().stream()
                .map(produtoDetalheMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "catalogo", key = "'todos'")
    @Transactional(readOnly = true)
    public List<ProdutoDetalheDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(produtoDetalheMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "catalogo", key = "'destaques'")
    @Transactional(readOnly = true)
    public List<ProdutoDetalheDTO> listarEmDestaque() {
        return produtoRepository.findByDestaqueTrueAndAtivoTrue().stream()
                .map(produtoDetalheMapper::toDTO)
                .collect(Collectors.toList());
    }
}