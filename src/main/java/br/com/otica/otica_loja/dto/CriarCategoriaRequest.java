package br.com.otica.otica_loja.dto;

public record CriarCategoriaRequest(
        String nome,
        String slug,
        String descricao
) {
}
