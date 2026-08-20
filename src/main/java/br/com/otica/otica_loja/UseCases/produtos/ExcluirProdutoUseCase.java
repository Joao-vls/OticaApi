package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoMidiaRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.service.CloudinaryStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcluirProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final ProdutoVarianteRepository varianteRepository;
    private final ProdutoMidiaRepository midiaRepository;
    private final CloudinaryStorageService cloudinaryStorageService;

    /**
     * Soft Delete do Produto:
     * Marca o produto e suas variantes como inativos e seta a data de deleção.
     */
    @Transactional
    public void excluir(UUID produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado com o ID: " + produtoId));

        // Obtém o OffsetDateTime atual diretamente
        OffsetDateTime agora = OffsetDateTime.now();

        // 1. Desativa e marca soft delete no produto pai
        produto.setDeletadoEm(agora);
        produto.setAtivo(false);

        // 2. Desativa e marca soft delete nas variantes associadas
        List<ProdutoVariante> variantes = varianteRepository.findByProduto(produto);
        for (ProdutoVariante variante : variantes) {
            variante.setDeletadoEm(agora);
            variante.setAtivo(false);
        }

        varianteRepository.saveAll(variantes);
        produtoRepository.save(produto);
    }

    /**
     * Hard Delete (Exclusão Definitiva) do Produto:
     * Remove do Cloudinary todas as mídias do produto e de suas variantes,
     * e apaga os registros de Mídias, Variantes e Produto do Banco de Dados.
     */
    @Transactional
    public void excluirDefinitivo(UUID produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado com o ID: " + produtoId));

        // 1. Buscar todas as mídias diretamente ligadas ao produto
        List<ProdutoMidia> midiasProduto = midiaRepository.findByProduto(produto);

        // 2. Buscar todas as variantes do produto
        List<ProdutoVariante> variantes = varianteRepository.findByProduto(produto);

        // 3. Para cada variante, apagar mídias no Cloudinary e coletá-las para apagar do BD
        for (ProdutoVariante variante : variantes) {
            List<ProdutoMidia> midiasVariante = midiaRepository.findByVariante(variante);

            for (ProdutoMidia midia : midiasVariante) {
                cloudinaryStorageService.deletarMidiaCompleta(midia);
            }

            midiaRepository.deleteAll(midiasVariante);
        }

        // 4. Apagar do Cloudinary e do BD as mídias diretas do produto
        for (ProdutoMidia midia : midiasProduto) {
            cloudinaryStorageService.deletarMidiaCompleta(midia);
        }
        midiaRepository.deleteAll(midiasProduto);

        // 5. Apagar todas as variantes no BD
        varianteRepository.deleteAll(variantes);

        // 6. Apagar o produto no BD
        produtoRepository.delete(produto);
    }
}