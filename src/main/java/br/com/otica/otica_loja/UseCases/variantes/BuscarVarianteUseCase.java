package br.com.otica.otica_loja.UseCases.variantes;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BuscarVarianteUseCase {

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    /**
     * Busca variante pelo ID.
     */
    public ProdutoVariante buscarPorId(UUID varianteId) {
        return varianteRepository.findById(varianteId)
                .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada."));
    }

    /**
     * Busca variante pelo SKU.
     */
    public ProdutoVariante buscarPorSku(String sku) {
        return varianteRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada com este SKU."));
    }

    /**
     * Busca variante pelo código de barras.
     */
    public ProdutoVariante buscarPorCodigoBarras(String codigoBarras) {
        return varianteRepository.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada com este código de barras."));
    }

    /**
     * Lista todas as variantes não deletadas (ativas ou inativas).
     */
    public List<ProdutoVariante> listarNaoDeletadas() {
        return varianteRepository.findByDeletadoEmIsNull();
    }

    /**
     * Lista todas as variantes deletadas (soft delete).
     */
    public List<ProdutoVariante> listarDeletadas() {
        return varianteRepository.findByDeletadoEmIsNotNull();
    }
}
