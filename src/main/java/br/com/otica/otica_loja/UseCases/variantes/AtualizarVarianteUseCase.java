package br.com.otica.otica_loja.UseCases.variantes;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.dto.ProdutoVarianteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AtualizarVarianteUseCase {

    private final ProdutoVarianteRepository varianteRepository;

    @Transactional
    public Map<String, ProdutoVariante> processarVariantes(Produto produto, List<ProdutoVarianteDTO> variantesDto) {
        Map<String, ProdutoVariante> variantesMapa = new HashMap<>();

        if (variantesDto == null || variantesDto.isEmpty()) {
            return variantesMapa;
        }

        for (ProdutoVarianteDTO dto : variantesDto) {
            ProdutoVariante varianteSalva = processarVarianteUnica(produto, dto);

            if (varianteSalva != null && Boolean.TRUE.equals(varianteSalva.getAtivo())) {
                if (dto.getRefVariante() != null && !dto.getRefVariante().isBlank()) {
                    variantesMapa.put(dto.getRefVariante(), varianteSalva);
                }
                if (varianteSalva.getId() != null) {
                    variantesMapa.put(varianteSalva.getId().toString(), varianteSalva);
                }
            }
        }

        return variantesMapa;
    }

    @Transactional
    public ProdutoVariante processarVarianteUnica(Produto produto, ProdutoVarianteDTO dto) {
        if (Boolean.TRUE.equals(dto.getRemover()) && dto.getId() != null) {
            ProdutoVariante varExistente = varianteRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada com ID: " + dto.getId()));

            varExistente.setAtivo(false);
            varExistente.setDeletadoEm(OffsetDateTime.now());
            varExistente.setAtualizadoEm(OffsetDateTime.now());
            return varianteRepository.save(varExistente);
        }

        ProdutoVariante variante;
        if (dto.getId() != null) {
            variante = varianteRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada com ID: " + dto.getId()));
        } else {
            variante = new ProdutoVariante();
            variante.setProduto(produto);
            variante.setCriadoEm(OffsetDateTime.now());

            if (dto.getSku() == null || dto.getSku().isBlank()) {
                throw new IllegalArgumentException("SKU é obrigatório para novas variantes.");
            }
            if (dto.getNome() == null || dto.getNome().isBlank()) {
                variante.setNome(produto.getNome());
            }
        }

        if (dto.getNome() != null) variante.setNome(dto.getNome());
        if (dto.getSku() != null) variante.setSku(dto.getSku());
        if (dto.getCodigoBarras() != null) variante.setCodigoBarras(dto.getCodigoBarras());
        if (dto.getColorName() != null) variante.setColorName(dto.getColorName());
        if (dto.getColorHex() != null) variante.setColorHex(dto.getColorHex());
        if (dto.getColorImagePath() != null) variante.setColorImagePath(dto.getColorImagePath());
        if (dto.getPesoGramas() != null) variante.setPesoGramas(dto.getPesoGramas());
        if (dto.getStock() != null) variante.setStock(dto.getStock());
        if (dto.getEstoqueMinimo() != null) variante.setEstoqueMinimo(dto.getEstoqueMinimo());
        if (dto.getPriceOverride() != null) variante.setPriceOverride(dto.getPriceOverride());
        if (dto.getAtivo() != null) variante.setAtivo(dto.getAtivo());

        variante.setAtualizadoEm(OffsetDateTime.now());
        return varianteRepository.save(variante);
    }
}