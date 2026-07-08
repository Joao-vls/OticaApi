package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExcluirProdutoUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Exclui (soft delete) um produto pelo ID.
     * Marca como inativo e registra a data de exclusão.
     */
    public void excluir(UUID produtoId) {
        // 1. Buscar produto
        Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
        if (produtoOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }

        Produto produto = produtoOpt.get();

        // 2. Soft delete: marcar como inativo e registrar data
        produto.setAtivo(false);
        produto.setDeletadoEm(OffsetDateTime.now());
        produto.setAtualizadoEm(OffsetDateTime.now());

        // 3. Persistir alterações
        produtoRepository.save(produto);
    }

    /**
     * Exclusão definitiva (hard delete).
     */
    public void excluirDefinitivo(UUID produtoId) {
        if (!produtoRepository.existsById(produtoId)) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }
        produtoRepository.deleteById(produtoId);
    }
}
