package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.util.List;

public record OpcaoLenteDTO(
        String uso,                  // ex: "LONGE", "PERTO", "SEM_GRAU"
        String descricaoUso,
        String tecnologia,           // ex: "LENTE_COMUM", "FOTOSSENSIVEL"
        String espessura,            // ex: "COMUM", "FINA", "ULTRAFINA"
        String limiteGrau,           // ex: "Miopia até -6.00..."
        String antirreflexo,         // ex: "FILTRO_AZUL", "FILTRO_VERDE"
        BigDecimal precoAdicional
) {}