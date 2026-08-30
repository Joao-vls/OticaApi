package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListarProdutosUseCase {

    private final ProdutoRepository produtoRepository;

    /**
     * Lista todos os produtos.
     */
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    /**
     * Lista todos os produtos ativos.
     */
    public List<Produto> listarAtivos() {
        return produtoRepository.findByAtivoTrue();
    }

    /**
     * Lista todos os produtos inativos.
     */
    public List<Produto> listarInativos() {
        return produtoRepository.findByAtivoFalse();
    }

    /**
     * Lista todos os produtos em destaque.
     */
    public List<Produto> listarEmDestaque() {
        return produtoRepository.findByDestaqueTrue();
    }

    /**
     * Lista todos os produtos não deletados (soft delete).
     */
    public List<Produto> listarNaoDeletados() {
        return produtoRepository.findByDeletadoEmIsNull();
    }

    /**
     * Lista todos os produtos deletados (soft delete).
     */
    public List<Produto> listarDeletados() {
        return produtoRepository.findByDeletadoEmIsNotNull();
    }

    /**
     * Lista produtos por categoria (N:N).
     */
    public List<Produto> listarPorCategoria(UUID categoriaId) {
        return produtoRepository.findByCategorias_Id(categoriaId);
    }

    /**
     * Lista produtos por marca.
     */
    public List<Produto> listarPorMarca(UUID marcaId) {
        return produtoRepository.findByMarcaId(marcaId);
    }

    /**
     * Lista produtos por nome (busca parcial).
     */
    public List<Produto> listarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }
}