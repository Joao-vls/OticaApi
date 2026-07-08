package br.com.otica.otica_loja.dto;

import org.springframework.web.multipart.MultipartFile;

public record AtualizarMarcaRequestDTO(
        String nome,
        String slug,
        String descricao,
        Boolean ativo,
        MultipartFile logoFile,
        String logoUrl,
        MultipartFile bannerFile,
        String bannerUrl
) {
}
