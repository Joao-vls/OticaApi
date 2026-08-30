package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.enums.GrauOculos;
import br.com.otica.otica_loja.enums.TipoMidia;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ProdutoRequestDTO(
        @NotNull(message = "A marca é obrigatória")
        UUID marcaId,

        @NotEmpty(message = "O produto deve ter pelo menos uma categoria associada")
        Set<UUID> categoriasIds,

        @NotBlank(message = "O nome do produto não pode estar em branco")
        String nome,

        @NotBlank(message = "O slug é obrigatório")
        String slug,

        String descricao,

        @NotNull(message = "O preço base é obrigatório")
        @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
        BigDecimal preco,

        Set<GrauOculos> grausDisponiveis,

        Boolean permiteLenteGrau,

        Boolean permiteReceita,

        String specs, // JSON String contendo especificações técnicas

        Boolean destaque,

        @NotEmpty(message = "O produto deve ter pelo menos uma variante")
        @Valid
        List<VarianteRequestDTO> variantes,

        @Valid
        List<MidiaRequestDTO> midias
) {
        public record VarianteRequestDTO(
                @NotBlank(message = "A referência da variante é obrigatória para associar às mídias")
                String refVariante,

                @NotBlank(message = "O nome da variante é obrigatório")
                String nome,

                @NotBlank(message = "O SKU é obrigatório")
                String sku,

                String codigoBarras,

                @NotBlank(message = "O nome da cor é obrigatório")
                String colorName,

                @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "O colorHex deve ser um valor hexadecimal válido")
                String colorHex,

                String colorImagePath,

                @NotNull(message = "O peso é obrigatório")
                @DecimalMin(value = "0.0", message = "O peso não pode ser negativo")
                BigDecimal pesoGramas,

                @NotNull(message = "O estoque é obrigatório")
                @Min(value = 0, message = "O estoque não pode ser negativo")
                Integer stock,

                @NotNull(message = "O estoque mínimo é obrigatório")
                @Min(value = 0, message = "O estoque mínimo não pode ser negativo")
                Integer estoqueMinimo,

                @DecimalMin(value = "0.0", message = "O preço sobrescrito não pode ser negativo")
                BigDecimal priceOverride
        ) {}

        public record MidiaRequestDTO(
                @NotBlank(message = "A referência da variante na mídia é obrigatória")
                String refVariante,

                @NotNull(message = "O tipo de mídia é obrigatório")
                TipoMidia tipo,

                String path,
                String urlExterna,          // URL enviada pelo front-end (ex: Cloudinary)
                String urlExternaThumbnail, // URL específica para miniaturas de vídeo
                String thumbnailPath,
                String posterPath,

                @NotNull(message = "A ordem de exibição da mídia é obrigatória")
                @Min(value = 0, message = "A ordem não pode ser negativa")
                Integer ordem
        ) {}
}