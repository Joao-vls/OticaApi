package br.com.otica.otica_loja.dto;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoPendenteLogisticaDTO(
        UUID id,
        Long numero,
        String clienteNome,
        String clienteTelefone,
        OffsetDateTime criadoEm,
        List<ItemResumoDTO> itens
) {}