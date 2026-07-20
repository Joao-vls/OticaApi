package br.com.otica.otica_loja.dto.cms;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record VitrineResponseDTO(
        String title,
        String subtitle,
        List<ProductCardDTO> products
) {
    public record ProductCardDTO(
            UUID id,
            String name,
            String slug,
            BigDecimal price,
            String image,          // Imagem principal (ex: primeira da lista de mídias)
            List<String> images,   // Galeria de até 5 imagens para o preview do Angular
            Integer totalCores     // 🔥 Adicionado para tornar as cores dinâmicas no Angular
    ) {}
}