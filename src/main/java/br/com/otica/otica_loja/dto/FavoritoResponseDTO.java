package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.Entity.Avaliacao.Favorito;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.enums.TipoMidia;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FavoritoResponseDTO(
        UUID produtoId,
        String nomeProduto,
        String imagemUrl,
        OffsetDateTime criadoEm
) {
    public static FavoritoResponseDTO fromEntity(Favorito favorito) {
        String imagem = null;

        if (favorito.getProduto() != null && favorito.getProduto().getMidias() != null) {
            imagem = favorito.getProduto().getMidias().stream()
                    // 1. Filtra apenas mídias do tipo IMAGE (ignora VIDEO)
                    .filter(m -> m.getTipo() == TipoMidia.IMAGE)
                    .map(ProdutoMidia::getPath)
                    .findFirst()
                    .orElse(null);

            // Fallback: se o produto só tiver vídeo cadastrado e nenhuma imagem, pega qualquer mídia
            if (imagem == null) {
                imagem = favorito.getProduto().getMidias().stream()
                        .map(ProdutoMidia::getPath)
                        .findFirst()
                        .orElse(null);
            }
        }

        return new FavoritoResponseDTO(
                favorito.getProduto().getId(),
                favorito.getProduto().getNome(),
                imagem,
                favorito.getCriadoEm()
        );
    }
}