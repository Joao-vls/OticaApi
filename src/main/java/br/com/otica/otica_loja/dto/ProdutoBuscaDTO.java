package br.com.otica.otica_loja.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoBuscaDTO {
    private UUID id;
    private String nome;
    private String slug;
    private BigDecimal preco;
    private String imagemPrincipal;
    private Boolean ativo;
}