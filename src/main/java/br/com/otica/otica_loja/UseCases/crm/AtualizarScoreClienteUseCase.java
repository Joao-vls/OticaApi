package br.com.otica.otica_loja.UseCases.crm;

import br.com.otica.otica_loja.Entity.CRM.CrmCliente;
import br.com.otica.otica_loja.Repository.CRM.CrmClienteRepository;
import br.com.otica.otica_loja.enums.NivelCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AtualizarScoreClienteUseCase {

    @Autowired
    private CrmClienteRepository clienteRepository;

    /**
     * Atualiza o score e nível de um cliente CRM.
     */
    public CrmCliente atualizarScore(UUID usuarioId) {
        // 1. Buscar cliente
        CrmCliente cliente = clienteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente CRM não encontrado."));

        // 2. Calcular novo score (exemplo simples)
        int score = 0;

        // Pontos por número de pedidos
        score += cliente.getTotalPedidos() * 10;

        // Pontos por valor gasto (cada R$100 = +5 pontos)
        score += cliente.getValorGasto().divide(BigDecimal.valueOf(100)).intValue() * 5;

        // Ajustar score mínimo
        if (score < 0) score = 0;

        cliente.setScore(score);

        // 3. Atualizar nível com base no score
        if (score >= 1000) {
            cliente.setNivel(NivelCliente.DIAMANTE);
        } else if (score >= 500) {
            cliente.setNivel(NivelCliente.OURO);
        } else if (score >= 200) {
            cliente.setNivel(NivelCliente.PRATA);
        } else {
            cliente.setNivel(NivelCliente.BRONZE);
        }

        // 4. Atualizar data
        cliente.setAtualizadoEm(OffsetDateTime.now());

        // 5. Persistir
        return clienteRepository.save(cliente);
    }
}
