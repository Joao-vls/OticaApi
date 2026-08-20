package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoMidiaRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.service.CloudinaryStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcluirProdutoVarianteUseCase {

    private final ProdutoVarianteRepository varianteRepository;
    private final ProdutoMidiaRepository midiaRepository;
    private final CloudinaryStorageService cloudinaryStorageService;

    /**
     * Soft Delete: Marca a variante como desativada e com data de exclusão.
     */
    @Transactional
    public void excluirSoft(UUID varianteId) {
        ProdutoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new NoSuchElementException("Variante não encontrada com o ID: " + varianteId));

        variante.setDeletadoEm(OffsetDateTime.from(LocalDateTime.now()));
        variante.setAtivo(false);
        varianteRepository.save(variante);
    }

    /**
     * Hard Delete: Apaga a variante, todas as mídias do banco e remove todos os arquivos (path, thumbnail, poster) do Cloudinary.
     */
    @Transactional
    public void excluirDefinitivo(UUID varianteId) {
        ProdutoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new NoSuchElementException("Variante não encontrada com o ID: " + varianteId));

        // 1. Buscar todas as mídias da variante
        List<ProdutoMidia> midias = midiaRepository.findByVariante(variante);

        // 2. Limpar arquivos do Cloudinary (path, thumbnail, poster)
        for (ProdutoMidia midia : midias) {
            cloudinaryStorageService.deletarMidiaCompleta(midia);
        }

        // 3. Apagar os registros de mídia do banco
        midiaRepository.deleteAll(midias);

        // 4. Apagar o registro da variante do banco
        varianteRepository.delete(variante);
    }

    /**
     * Remove uma mídia isolada da variante (BD e Cloudinary).
     */
    @Transactional
    public void excluirMidiaIndividual(UUID midiaId) {
        ProdutoMidia midia = midiaRepository.findById(midiaId)
                .orElseThrow(() -> new NoSuchElementException("Mídia não encontrada com o ID: " + midiaId));

        cloudinaryStorageService.deletarMidiaCompleta(midia);
        midiaRepository.delete(midia);
    }
}