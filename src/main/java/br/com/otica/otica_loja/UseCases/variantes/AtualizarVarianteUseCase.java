package br.com.otica.otica_loja.UseCases.variantes;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AtualizarVarianteUseCase {

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    /**
     * Atualiza os dados de uma variante existente.
     */
    public ProdutoVariante atualizar(UUID varianteId,
                                     String nome,
                                     String sku,
                                     String codigoBarras,
                                     String colorName,
                                     String colorHex,
                                     String colorImagePath,
                                     BigDecimal pesoGramas,
                                     Integer stock,
                                     Integer estoqueMinimo,
                                     BigDecimal priceOverride,
                                     Boolean ativo) {

        // 1. Buscar variante existente
        Optional<ProdutoVariante> varianteOpt = varianteRepository.findById(varianteId);
        if (varianteOpt.isEmpty()) {
            throw new IllegalArgumentException("Variante não encontrada.");
        }

        ProdutoVariante variante = varianteOpt.get();

        // 2. Atualizar campos informados
        if (nome != null && !nome.isBlank()) {
            variante.setNome(nome);
        }

        if (sku != null && !sku.isBlank()) {
            if (!variante.getSku().equalsIgnoreCase(sku) && varianteRepository.findBySku(sku).isPresent()) {
                throw new IllegalArgumentException("Já existe uma variante com este SKU.");
            }
            variante.setSku(sku);
        }

        if (codigoBarras != null && !codigoBarras.isBlank()) {
            if (variante.getCodigoBarras() == null || !variante.getCodigoBarras().equalsIgnoreCase(codigoBarras)) {
                if (varianteRepository.findByCodigoBarras(codigoBarras).isPresent()) {
                    throw new IllegalArgumentException("Já existe uma variante com este código de barras.");
                }
            }
            variante.setCodigoBarras(codigoBarras);
        }

        if (colorName != null) {
            variante.setColorName(colorName);
        }

        if (colorHex != null) {
            variante.setColorHex(colorHex);
        }

        if (colorImagePath != null) {
            variante.setColorImagePath(colorImagePath);
        }

        if (pesoGramas != null) {
            variante.setPesoGramas(pesoGramas);
        }

        if (stock != null) {
            variante.setStock(stock);
        }

        if (estoqueMinimo != null) {
            variante.setEstoqueMinimo(estoqueMinimo);
        }

        if (priceOverride != null) {
            variante.setPriceOverride(priceOverride);
        }

        if (ativo != null) {
            variante.setAtivo(ativo);
        }

        // 3. Atualizar timestamp
        variante.setAtualizadoEm(OffsetDateTime.now());

        // 4. Persistir alterações
        return varianteRepository.save(variante);
    }
}
