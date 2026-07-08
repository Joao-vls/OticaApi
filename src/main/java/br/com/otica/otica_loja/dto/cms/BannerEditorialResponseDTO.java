package br.com.otica.otica_loja.dto.cms;

public record BannerEditorialResponseDTO(
        String layoutTipo,      // "VERTICAL" ou "HORIZONTAL"
        String textoMarca,
        String logoSrc,
        BannerEditorialSectionDTO secaoUm,   // Topo ou Esquerda
        BannerEditorialSectionDTO secaoDois   // Fundo ou Direita
) {
}