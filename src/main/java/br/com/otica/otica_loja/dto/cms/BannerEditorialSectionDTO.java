package br.com.otica.otica_loja.dto.cms;

import java.math.BigDecimal;

public record BannerEditorialSectionDTO(
        String media,
        String titulo,
        String tituloDestaque,
        String descricao,
        String produtoNome,
        BigDecimal preco,
        Integer desconto,
        String linkUrl
) {
}