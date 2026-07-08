package br.com.otica.otica_loja.dto.cms;

import java.math.BigDecimal;

public record PromoSectionDTO(

        String media,

        String frameText,
        String highlightedWord,

        String title,
        String highlightedTitle,

        String productName,

        String description,

        BigDecimal price,

        Integer discount,

        String productSlug

) {
}