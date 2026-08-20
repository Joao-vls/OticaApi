package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.Entity.Catalogo.Categoria;
import java.util.UUID;

public record CategoriaResponseDTO(
        UUID id,
        String nome,
        String slug,
        boolean ativo
) {
    public static CategoriaResponseDTO fromEntity(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getSlug(),
                Boolean.TRUE.equals(categoria.getAtivo())
        );
    }
}