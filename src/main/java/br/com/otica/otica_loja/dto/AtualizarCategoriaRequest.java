package br.com.otica.otica_loja.dto;

public record AtualizarCategoriaRequest (
        String nome,
        String slug,
        String descricao,
        Boolean ativo
) {
}
