package br.com.otica.otica_loja.UseCases.variantes;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CriarVarianteUseCase {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    /**
     * Cria uma nova variante para um produto existente.
     */
    public ProdutoVariante cria(UUID produtoId,
                                 String nome,
                                 String sku,
                                 String codigoBarras,
                                 String colorName,
                                 String colorHex,
                                 String colorImagePath,
                                 BigDecimal pesoGramas,
                                 Integer stock,
                                 Integer estoqueMinimo,
                                 BigDecimal priceOverride) {

        // 1. Verificar se o produto existe
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        // 2. Verificar duplicidade de SKU e código de barras
        if (varianteRepository.findBySku(sku).isPresent()) {
            throw new IllegalArgumentException("Já existe uma variante com este SKU.");
        }
        if (codigoBarras != null && varianteRepository.findByCodigoBarras(codigoBarras).isPresent()) {
            throw new IllegalArgumentException("Já existe uma variante com este código de barras.");
        }

        // 3. Criar nova variante
        ProdutoVariante variante = new ProdutoVariante();
        variante.setId(UUID.randomUUID());
        variante.setProduto(produto);
        variante.setNome(nome);
        variante.setSku(sku);
        variante.setCodigoBarras(codigoBarras);
        variante.setColorName(colorName);
        variante.setColorHex(colorHex);
        variante.setColorImagePath(colorImagePath);
        variante.setPesoGramas(pesoGramas);
        variante.setStock(stock != null ? stock : 0);
        variante.setEstoqueMinimo(estoqueMinimo != null ? estoqueMinimo : 0);
        variante.setPriceOverride(priceOverride);
        variante.setAtivo(true);
        variante.setCriadoEm(OffsetDateTime.now());
        variante.setAtualizadoEm(OffsetDateTime.now());

        // 4. Persistir no banco
        return varianteRepository.save(variante);
    }
}