package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.Entity.Catalogo.Marca;
import java.util.UUID;

public record MarcaResponseDTO(
        UUID id,
        String nome,
        String slug,
        boolean ativo
) {
    public static MarcaResponseDTO fromEntity(Marca marca) {
        return new MarcaResponseDTO(
                marca.getId(),
                marca.getNome(),
                marca.getSlug(),
                Boolean.TRUE.equals(marca.getAtivo())
        );
    }
}