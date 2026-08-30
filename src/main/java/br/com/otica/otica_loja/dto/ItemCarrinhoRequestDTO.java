package br.com.otica.otica_loja.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemCarrinhoRequestDTO(
        UUID produtoId,
        UUID varianteId,
        Integer quantidade,

        // Dados de personalização da lente (Preenchido apenas quando permiteLenteGrau = true)
        String usoLente,         // ex: "LONGE"
        String tecnologiaLente,  // ex: "LENTE_COMUM"
        String espessuraLente,   // ex: "ULTRAFINA"
        String antirreflexoLente,// ex: "FILTRO_AZUL"
        UUID receitaMedicaId     // FK opcional da ReceitaMedica enviada pelo cliente
) {}