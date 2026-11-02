package br.com.otica.otica_loja.dto.cms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VitrineResponseDTO {

    private String title;
    private String subtitle;
    private List<ProductCardDTO> products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCardDTO {
        private UUID id;
        private String name;
        private String slug;
        private BigDecimal price;
        private String image;          // Imagem principal (ex: primeira da lista de mídias)
        private List<String> images;   // Galeria de até 5 imagens para o preview do Angular
        private Integer totalCores;    // 🔥 Adicionado para tornar as cores dinâmicas no Angular
    }
}