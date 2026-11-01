package br.com.otica.otica_loja.UseCases.variantes;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletarVarianteUseCase {

    private final ProdutoVarianteRepository varianteRepository;
    @Caching(evict = {
            @CacheEvict(value = "produto", allEntries = true),
            @CacheEvict(value = "catalogo", allEntries = true),
            @CacheEvict(value = "vitrines", allEntries = true),
            @CacheEvict(value = "filtros", allEntries = true)
    })
    @Transactional
    public void excluirSoft(UUID varianteId) {
        ProdutoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada com ID: " + varianteId));

        variante.setAtivo(false);
        variante.setDeletadoEm(OffsetDateTime.now());
        variante.setAtualizadoEm(OffsetDateTime.now());
        varianteRepository.save(variante);
    }
    @Caching(evict = {
            @CacheEvict(value = "produto", allEntries = true),
            @CacheEvict(value = "catalogo", allEntries = true),
            @CacheEvict(value = "vitrines", allEntries = true),
            @CacheEvict(value = "filtros", allEntries = true)
    })
    @Transactional
    public void excluirDefinitivo(UUID varianteId) {
        if (!varianteRepository.existsById(varianteId)) {
            throw new IllegalArgumentException("Variante não encontrada com ID: " + varianteId);
        }
        varianteRepository.deleteById(varianteId);
    }
}