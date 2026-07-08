package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProdutoRequestDTO(
        UUID marcaId,
        UUID categoriaId,
        String nome,
        String slug,
        String descricao,
        BigDecimal preco,
        String categoriaOculos,
        String specs,
        Boolean destaque,
        List<VarianteRequestDTO> variantes,
        List<MidiaRequestDTO> midias // Lista unificada aqui
) {
    public record VarianteRequestDTO(
            String refVariante,
            String nome,
            String sku,
            String codigoBarras,
            String colorName,
            String colorHex,
            String colorImagePath,
            BigDecimal pesoGramas,
            Integer stock,
            Integer estoqueMinimo,
            BigDecimal priceOverride
    ) {}

    public record MidiaRequestDTO(
            String refVariante,
            String tipo,        // image, video, 3d
            String path,        // Mantido caso queira enviar um path padrão
            String urlExterna,  // <--- Adicionado: Se o usuário colar uma URL em vez de fazer upload
            String thumbnailPath,
            String posterPath,
            Integer ordem
    ) {}
}