package br.com.otica.otica_loja.mapper;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.dto.ProdutoDetalheDTO;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class ProdutoDetalheMapper {

    public ProdutoDetalheDTO toDTO(Produto produto) {
        ProdutoDetalheDTO dto = new ProdutoDetalheDTO();

        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setSlug(produto.getSlug());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());

        // 🔥 String é imutável e segura contra Proxies do Hibernate
        dto.setSpecs(produto.getSpecs());

        dto.setPermiteLenteGrau(produto.getPermiteLenteGrau());
        dto.setPermiteReceita(produto.getPermiteReceita());

        // 🔥 Instancia um HashSet Java puro para remover o PersistentSet do Hibernate
        if (produto.getGrausDisponiveis() != null) {
            dto.setGrausDisponiveis(
                    produto.getGrausDisponiveis().stream()
                            .collect(Collectors.toSet())
            );
        }

        dto.setDestaque(produto.getDestaque());
        dto.setAtivo(produto.getAtivo());

        // Marca
        if (produto.getMarca() != null) {
            dto.setMarca(new ProdutoDetalheDTO.MarcaDTO(
                    produto.getMarca().getId(),
                    produto.getMarca().getNome(),
                    produto.getMarca().getSlug()
            ));
        }

        // Categorias
        if (produto.getCategorias() != null) {
            dto.setCategorias(produto.getCategorias().stream()
                    .map(c -> new ProdutoDetalheDTO.CategoriaDTO(
                            c.getId(),
                            c.getNome(),
                            c.getSlug()
                    ))
                    .collect(Collectors.toList()));
        }

        // Variantes
        if (produto.getVariantes() != null) {
            dto.setVariantes(produto.getVariantes().stream()
                    .map(v -> new ProdutoDetalheDTO.ProdutoVarianteDTO(
                            v.getId(),
                            v.getNome(),
                            v.getSku(),
                            v.getCodigoBarras(),
                            v.getColorName(),
                            v.getColorHex(),
                            v.getColorImagePath(),
                            v.getPesoGramas(),
                            v.getPriceOverride(),
                            v.getStock(),
                            v.getEstoqueMinimo(),
                            v.getAtivo()
                    ))
                    .collect(Collectors.toList()));
        }

        // Mídias
        if (produto.getMidias() != null) {
            dto.setMidias(produto.getMidias().stream()
                    .map(m -> {
                        ProdutoDetalheDTO.VarianteRefDTO varRef = null;

                        if (m.getVariante() != null) {
                            varRef = new ProdutoDetalheDTO.VarianteRefDTO(
                                    m.getVariante().getId()
                            );
                        }

                        return new ProdutoDetalheDTO.ProdutoMidiaDTO(
                                m.getId(),
                                m.getTipo(),
                                m.getPath(),
                                m.getThumbnailPath(),
                                m.getPosterPath(),
                                m.getOrdem(),
                                varRef
                        );
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}