package br.com.otica.otica_loja.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtivarAssinaturaAdminRequestDTO(
        @NotBlank(message = "O canal de pagamento é obrigatório (ex: WhatsApp - PIX)")
        String canalPagamento,

        String anotacoesAdmin,

        @NotNull(message = "A validade em meses é obrigatória")
        @Min(value = 1, message = "A assinatura deve ter no mínimo 1 mês de validade")
        Integer mesesValidade
) {}