package br.com.otica.otica_loja.UseCases.variantes;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExcluirVarianteUseCase {

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    /**
     * Exclui (soft delete) uma variante pelo ID.
     * Marca como inativa e registra a data de exclusão.
     */
    public void excluir(UUID varianteId) {
        // 1. Buscar variante
        Optional<ProdutoVariante> varianteOpt = varianteRepository.findById(varianteId);
        if (varianteOpt.isEmpty()) {
            throw new IllegalArgumentException("Variante não encontrada.");
        }

        ProdutoVariante variante = varianteOpt.get();

        // 2. Soft delete: marcar como inativa e registrar data
        variante.setAtivo(false);
        variante.setDeletadoEm(OffsetDateTime.now());
        variante.setAtualizadoEm(OffsetDateTime.now());

        // 3. Persistir alterações
        varianteRepository.save(variante);
    }

    /**
     * Exclusão definitiva (hard delete).
     */
    public void excluirDefinitivo(UUID varianteId) {
        if (!varianteRepository.existsById(varianteId)) {
            throw new IllegalArgumentException("Variante não encontrada.");
        }
        varianteRepository.deleteById(varianteId);
    }
}
