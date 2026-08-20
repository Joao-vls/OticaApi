package br.com.otica.otica_loja.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProdutoUpdateRequestDTO {

    private String nome;
    private String slug;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private String specs;
    private UUID marcaId;
    private UUID categoriaId;
    private Boolean destaque;
    private Boolean ativo;

    private List<ProdutoVarianteDTO> variantes;
    private List<ProdutoMidiaUpdateDTO> midias; // 👈 Adicionado suporte a mídias
}