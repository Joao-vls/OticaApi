package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.enums.GrauOculos;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ProdutoUpdateRequestDTO {
    private String nome;
    private String slug;
    private String descricao;
    private BigDecimal preco;
    private UUID marcaId;
    private Set<UUID> categoriasIds;
    private Set<GrauOculos> grausDisponiveis;
    private Boolean permiteLenteGrau;
    private Boolean permiteReceita;
    private String specs;
    private Boolean destaque;
    private Boolean ativo;
    private List<ProdutoVarianteDTO> variantes;
    private List<ProdutoMidiaUpdateDTO> midias;
}