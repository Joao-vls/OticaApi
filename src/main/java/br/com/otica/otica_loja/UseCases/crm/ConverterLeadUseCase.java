package br.com.otica.otica_loja.UseCases.crm;

import br.com.otica.otica_loja.Entity.CRM.CrmCliente;
import br.com.otica.otica_loja.Entity.CRM.Lead;
import br.com.otica.otica_loja.Repository.CRM.CrmClienteRepository;
import br.com.otica.otica_loja.Repository.CRM.LeadRepository;
import br.com.otica.otica_loja.enums.NivelCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ConverterLeadUseCase {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private CrmClienteRepository crmClienteRepository;

    /**
     * Converte um lead em cliente CRM.
     */
    public CrmCliente converterLead(UUID leadId, UUID usuarioId) {
        // 1. Buscar lead
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead não encontrado."));

        if (lead.getConvertido()) {
            throw new IllegalStateException("Este lead já foi convertido em cliente.");
        }

        // 2. Atualizar lead como convertido
        lead.setConvertido(true);
        leadRepository.save(lead);

        // 3. Criar cliente CRM
        CrmCliente cliente = new CrmCliente();
        cliente.setUsuarioId(usuarioId);
        cliente.setScore(0);
        cliente.setTotalPedidos(0);
        cliente.setValorGasto(BigDecimal.ZERO);
        cliente.setUltimoPedidoEm(null);
        cliente.setNivel(NivelCliente.BRONZE);
        cliente.setAtualizadoEm(OffsetDateTime.now());

        return crmClienteRepository.save(cliente);
    }
}
