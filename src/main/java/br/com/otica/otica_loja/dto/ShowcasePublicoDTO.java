package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ShowcasePublicoDTO(
        UUID id,
        String marcaNome,
        String modeloNome,
        String username,
        String thumbnailPath,
        String videoPath,
        Long curtidas,
        Long visualizacoes,
        ProdutoVinculadoDTO produto
) {
    public record ProdutoVinculadoDTO(
            UUID id,
            String nome,
            String slug,
            String imagemPrincipal, // Ajuste para a URL da imagem principal do seu produto
            BigDecimal preco
    ) {}
}