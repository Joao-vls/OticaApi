package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.dto.ProdutoDetalheDTO;
import br.com.otica.otica_loja.mapper.ProdutoDetalheMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PesquisarProdutosUseCase {

    private final ProdutoRepository produtoRepository;
    private final ProdutoDetalheMapper produtoDetalheMapper;

    @Transactional(readOnly = true)
    public List<ProdutoDetalheDTO> pesquisarPorTextoLivre(String termo) {

        return produtoRepository.pesquisaGlobalCompleta(termo).stream()
                .map(produtoDetalheMapper::toDTO)
                .collect(Collectors.toList());
    }
}