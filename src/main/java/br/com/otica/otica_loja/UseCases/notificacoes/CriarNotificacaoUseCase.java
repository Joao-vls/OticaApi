package br.com.otica.otica_loja.UseCases.notificacoes;

import br.com.otica.otica_loja.Entity.CRM.Notificacao;
import br.com.otica.otica_loja.Repository.CRM.NotificacaoRepository;
import br.com.otica.otica_loja.enums.TipoNotificacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CriarNotificacaoUseCase {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    /**
     * Cria uma nova notificação para um usuário.
     */
    public Notificacao criarNotificacao(UUID usuarioId, String titulo, String mensagem, TipoNotificacao tipo) {
        // 1. Criar objeto de notificação
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuarioId(usuarioId);
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setVisualizada(false);
        notificacao.setCriadoEm(OffsetDateTime.now());

        // 2. Persistir no banco
        return notificacaoRepository.save(notificacao);
    }
}
