package br.com.otica.otica_loja.dto;

import org.springframework.web.multipart.MultipartFile;

public record CriarMarcaRequestDTO (
        String nome,
        String slug,
        String descricao,
        MultipartFile logoFile,
        String logoUrl,
        MultipartFile bannerFile,
        String bannerUrl
){}
