package br.com.otica.otica_loja.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemVendaDTO {
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
}