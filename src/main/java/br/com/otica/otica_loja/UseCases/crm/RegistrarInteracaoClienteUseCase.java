package br.com.otica.otica_loja.UseCases.crm;

import br.com.otica.otica_loja.Entity.CRM.CrmInteracao;
import br.com.otica.otica_loja.Repository.CRM.CrmInteracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class RegistrarInteracaoClienteUseCase {

    @Autowired
    private CrmInteracaoRepository interacaoRepository;

    /**
     * Registra uma nova interação de cliente no CRM.
     */
    public CrmInteracao registrarInteracao(UUID usuarioId, String tipo, String descricao) {
        // 1. Criar interação
        CrmInteracao interacao = new CrmInteracao();
        interacao.setUsuarioId(usuarioId);
        interacao.setTipo(tipo);
        interacao.setDescricao(descricao);
        interacao.setCriadoEm(OffsetDateTime.now());

        // 2. Persistir
        return interacaoRepository.save(interacao);
    }
}
