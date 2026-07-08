package br.com.otica.otica_loja.UseCases.notificacoes;

import br.com.otica.otica_loja.Entity.CRM.Notificacao;
import br.com.otica.otica_loja.Repository.CRM.NotificacaoRepository;
import br.com.otica.otica_loja.enums.TipoNotificacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ListarNotificacoesUseCase {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    /**
     * Lista todas as notificações de um usuário.
     */
    public List<Notificacao> listarPorUsuario(UUID usuarioId) {
        return notificacaoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Lista notificações não visualizadas de um usuário.
     */
    public List<Notificacao> listarNaoVisualizadas(UUID usuarioId) {
        return notificacaoRepository.findByUsuarioIdAndVisualizadaFalse(usuarioId);
    }

    /**
     * Lista notificações visualizadas de um usuário.
     */
    public List<Notificacao> listarVisualizadas(UUID usuarioId) {
        return notificacaoRepository.findByUsuarioIdAndVisualizadaTrue(usuarioId);
    }

    /**
     * Lista notificações por tipo.
     */
    public List<Notificacao> listarPorTipo(TipoNotificacao tipo) {
        return notificacaoRepository.findByTipo(tipo);
    }

    /**
     * Lista notificações criadas após uma data.
     */
    public List<Notificacao> listarCriadasDepoisDe(OffsetDateTime data) {
        return notificacaoRepository.findByCriadoEmAfter(data);
    }

    /**
     * Lista notificações criadas antes de uma data.
     */
    public List<Notificacao> listarCriadasAntesDe(OffsetDateTime data) {
        return notificacaoRepository.findByCriadoEmBefore(data);
    }
}
