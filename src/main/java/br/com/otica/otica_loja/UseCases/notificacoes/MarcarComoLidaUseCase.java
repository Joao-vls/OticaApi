package br.com.otica.otica_loja.UseCases.notificacoes;

import br.com.otica.otica_loja.Entity.CRM.Notificacao;
import br.com.otica.otica_loja.Repository.CRM.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MarcarComoLidaUseCase {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    /**
     * Marca uma notificação como lida.
     */
    public Notificacao marcarComoLida(UUID notificacaoId) {
        // 1. Buscar notificação
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada."));

        if (Boolean.TRUE.equals(notificacao.getVisualizada())) {
            throw new IllegalStateException("Esta notificação já foi marcada como lida.");
        }

        // 2. Atualizar status
        notificacao.setVisualizada(true);

        // 3. Persistir
        return notificacaoRepository.save(notificacao);
    }
}
