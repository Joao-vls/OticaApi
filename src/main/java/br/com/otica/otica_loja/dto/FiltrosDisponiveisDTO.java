package br.com.otica.otica_loja.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class FiltrosDisponiveisDTO {
    private BigDecimal precoMinimo;
    private BigDecimal precoMaximo;
    private Set<String> marcasDisponiveis;
    private Set<String> categoriasDisponiveis;
    private Set<String> coresDisponiveis;
    private Set<String> grausDisponiveis;
    private Map<String, Set<String>> specs;
}