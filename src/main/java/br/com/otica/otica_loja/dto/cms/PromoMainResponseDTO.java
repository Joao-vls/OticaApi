package br.com.otica.otica_loja.dto.cms;

public record PromoMainResponseDTO(

        String brandName,
        String logoSrc,

        PromoSectionDTO topSection,
        PromoSectionDTO bottomSection

) {
}