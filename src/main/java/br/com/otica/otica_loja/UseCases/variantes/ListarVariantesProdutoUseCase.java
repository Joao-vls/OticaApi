package br.com.otica.otica_loja.UseCases.variantes;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarVariantesProdutoUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    /**
     * Lista todas as variantes de um produto.
     */
    public List<ProdutoVariante> listarTodas(UUID produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        return varianteRepository.findByProduto(produto);
    }

    /**
     * Lista variantes ativas de um produto.
     */
    public List<ProdutoVariante> listarAtivas(UUID produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        return varianteRepository.findByProdutoAndAtivoTrue(produto);
    }

    /**
     * Lista variantes inativas de um produto.
     */
    public List<ProdutoVariante> listarInativas(UUID produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        return varianteRepository.findByProdutoAndAtivoFalse(produto);
    }
}
