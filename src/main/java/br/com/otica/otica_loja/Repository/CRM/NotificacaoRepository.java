package br.com.otica.otica_loja.Repository.CRM;

import br.com.otica.otica_loja.Entity.CRM.Notificacao;
import br.com.otica.otica_loja.enums.TipoNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {

    // Buscar todas as notificações de um usuário
    List<Notificacao> findByUsuarioId(UUID usuarioId);

    // Buscar notificações não visualizadas de um usuário
    List<Notificacao> findByUsuarioIdAndVisualizadaFalse(UUID usuarioId);

    // Buscar notificações visualizadas de um usuário
    List<Notificacao> findByUsuarioIdAndVisualizadaTrue(UUID usuarioId);

    // Buscar notificações por tipo
    List<Notificacao> findByTipo(TipoNotificacao tipo);

    // Buscar notificações criadas após uma data
    List<Notificacao> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar notificações criadas antes de uma data
    List<Notificacao> findByCriadoEmBefore(OffsetDateTime data);
}
