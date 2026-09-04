package br.com.otica.otica_loja.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UltimaVendaDTO {
    private Long numeroPedido;
    private String pedidoId;
    private String nomeCliente; // 👈 Agora armazenará o Nome, não o ID
    private BigDecimal total;
    private String status;
    private String dataHora;
    private List<ItemVendaDTO> itens; // 👈 Lista de itens comprados
}