package br.com.otica.otica_loja.UseCases.crm;

import br.com.otica.otica_loja.Entity.CRM.Lead;
import br.com.otica.otica_loja.Repository.CRM.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class CriarLeadUseCase {

    @Autowired
    private LeadRepository leadRepository;

    /**
     * Cria um novo lead no CRM.
     */
    public Lead criarLead(String nome, String email, String telefone, String origem, String observacao) {
        // 1. Validar duplicidade por e-mail
        if (email != null && leadRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe um lead com este e-mail.");
        }

        // 2. Criar lead
        Lead lead = new Lead();
        lead.setNome(nome);
        lead.setEmail(email);
        lead.setTelefone(telefone);
        lead.setOrigem(origem);
        lead.setObservacao(observacao);
        lead.setConvertido(false);
        lead.setCriadoEm(OffsetDateTime.now());

        // 3. Persistir
        return leadRepository.save(lead);
    }
}
