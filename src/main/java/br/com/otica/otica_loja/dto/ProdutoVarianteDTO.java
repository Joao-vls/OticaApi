package br.com.otica.otica_loja.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProdutoVarianteDTO {

    // Se informado -> Atualiza a variante existente
    // Se nulo -> Cria uma nova variante
    private UUID id;

    // Referência temporária/identificadora para vincular mídias no frontend
    private String refVariante;

    private String nome;
    private String sku;
    private String codigoBarras;
    private String colorName;
    private String colorHex;
    private String colorImagePath;
    private BigDecimal pesoGramas;
    private Integer stock;
    private Integer estoqueMinimo;
    private BigDecimal priceOverride;
    private Boolean ativo;

    // Flag para exclusão da variante na atualização
    private Boolean remover = false;
}