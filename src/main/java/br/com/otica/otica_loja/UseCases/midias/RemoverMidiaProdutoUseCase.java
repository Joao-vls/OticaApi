package br.com.otica.otica_loja.UseCases.midias;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoMidiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoverMidiaProdutoUseCase {

    @Autowired
    private ProdutoMidiaRepository midiaRepository;

    /**
     * Remove definitivamente uma mídia pelo ID (hard delete).
     */
    @Caching(evict = {
            @CacheEvict(value = "produto", allEntries = true),
            @CacheEvict(value = "catalogo", allEntries = true),
            @CacheEvict(value = "vitrines", allEntries = true),
            @CacheEvict(value = "filtros", allEntries = true)
    })
    public void remover(UUID midiaId) {
        if (!midiaRepository.existsById(midiaId)) {
            throw new IllegalArgumentException("Mídia não encontrada.");
        }
        midiaRepository.deleteById(midiaId);
    }
}
