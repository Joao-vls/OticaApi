package br.com.otica.otica_loja.UseCases.chat;

import br.com.otica.otica_loja.Entity.Atendimento.ChatConversa;
import br.com.otica.otica_loja.Repository.Atendimento.ChatConversaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class FinalizarConversaUseCase {

    @Autowired
    private ChatConversaRepository conversaRepository;

    /**
     * Finaliza uma conversa de atendimento.
     */
    public ChatConversa finalizarConversa(UUID conversaId) {
        // 1. Buscar conversa
        ChatConversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new IllegalArgumentException("Conversa não encontrada."));

        if ("encerrado".equalsIgnoreCase(conversa.getStatus())) {
            throw new IllegalStateException("A conversa já está encerrada.");
        }

        // 2. Atualizar status e data
        conversa.setStatus("encerrado");
        conversa.setAtualizadoEm(OffsetDateTime.now());

        // 3. Persistir alterações
        return conversaRepository.save(conversa);
    }
}
