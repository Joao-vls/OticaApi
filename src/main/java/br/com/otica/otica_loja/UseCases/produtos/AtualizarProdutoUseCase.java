package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AtualizarProdutoUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Atualiza os dados de um produto existente.
     */
    public Produto atualizar(UUID produtoId,
                             UUID marcaId,
                             UUID categoriaId,
                             String nome,
                             String slug,
                             String descricao,
                             BigDecimal preco,
                             String categoriaOculos,
                             String specs,
                             Boolean destaque,
                             Boolean ativo) {

        // 1. Buscar produto existente
        Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
        if (produtoOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }

        Produto produto = produtoOpt.get();

        // 2. Atualizar campos informados
        if (nome != null && !nome.isBlank()) {
            produto.setNome(nome);
        }

        if (slug != null && !slug.isBlank()) {
            // Verificar duplicidade de slug
            if (!produto.getSlug().equalsIgnoreCase(slug) && produtoRepository.findBySlug(slug).isPresent()) {
                throw new IllegalArgumentException("Já existe um produto com este slug.");
            }
            produto.setSlug(slug);
        }

        if (descricao != null) {
            produto.setDescricao(descricao);
        }

        if (preco != null) {
            produto.setPreco(preco);
        }

        if (categoriaOculos != null) {
            produto.setCategoriaOculos(categoriaOculos);
        }

        if (specs != null) {
            produto.setSpecs(specs);
        }

        if (marcaId != null) {
            produto.setMarcaId(marcaId);
        }

        if (categoriaId != null) {
            produto.setCategoriaId(categoriaId);
        }

        if (destaque != null) {
            produto.setDestaque(destaque);
        }

        if (ativo != null) {
            produto.setAtivo(ativo);
        }

        // 3. Atualizar timestamp
        produto.setAtualizadoEm(OffsetDateTime.now());

        // 4. Persistir alterações
        return produtoRepository.save(produto);
    }
}
