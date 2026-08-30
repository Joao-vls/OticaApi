package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarProdutoPorVarianteUseCase {

    private final ProdutoVarianteRepository produtoVarianteRepository;

    /**
     * Localiza a variante pelo seu ID e retorna o produto pai associado a ela.
     */
    @Transactional(readOnly = true)
    public Produto executar(String varianteIdStr) {
        try {
            UUID varianteId = UUID.fromString(varianteIdStr);

            ProdutoVariante variante = produtoVarianteRepository.findById(varianteId)
                    .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada."));

            Produto produto = variante.getProduto();
            if (produto == null) {
                throw new IllegalArgumentException("Produto não associado a esta variante.");
            }

            return produto;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ID de variante inválido ou produto não encontrado.");
        }
    }
}