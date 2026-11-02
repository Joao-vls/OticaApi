package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.enums.GrauOculos;
import br.com.otica.otica_loja.enums.TipoMidia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDetalheDTO {

    private UUID id;
    private String nome;
    private String slug;
    private String descricao;
    private BigDecimal preco;
    private MarcaDTO marca;
    private List<CategoriaDTO> categorias;
    private List<ProdutoVarianteDTO> variantes;
    private List<ProdutoMidiaDTO> midias;
    private String specs;
    private Boolean permiteLenteGrau;
    private Boolean permiteReceita;
    private Set<GrauOculos> grausDisponiveis;
    private Boolean destaque;
    private Boolean ativo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarcaDTO {
        private UUID id;
        private String nome;
        private String slug;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaDTO {
        private UUID id;
        private String nome;
        private String slug;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProdutoVarianteDTO {
        private UUID id;
        private String nome;
        private String sku;
        private String codigoBarras;
        private String colorName;
        private String colorHex;
        private String colorImagePath;
        private BigDecimal pesoGramas;
        private BigDecimal priceOverride;
        private Integer stock;
        private Integer estoqueMinimo;
        private Boolean ativo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProdutoMidiaDTO {
        private UUID id;
        private TipoMidia tipo;
        private String path;
        private String thumbnailPath;
        private String posterPath;
        private Integer ordem;

        // 🔥 Estrutura aninhada para não quebrar o código Angular: m.variante?.id
        private VarianteRefDTO variante;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VarianteRefDTO {
        private UUID id;
    }
}