package br.com.otica.otica_loja.Repository.Atendimento;

import br.com.otica.otica_loja.Entity.Atendimento.ChatConversa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatConversaRepository extends JpaRepository<ChatConversa, UUID> {

    // Buscar conversas por usuário
    List<ChatConversa> findByUsuarioId(UUID usuarioId);

    // Buscar conversas por sessão anônima
    List<ChatConversa> findBySessaoAnonima(String sessaoAnonima);

    // Buscar conversas por canal (site, app, whatsapp, etc.)
    List<ChatConversa> findByCanal(String canal);

    // Buscar conversas por status (aberto, em_atendimento, encerrado)
    List<ChatConversa> findByStatus(String status);

    // Buscar conversa ativa de um usuário
    Optional<ChatConversa> findByUsuarioIdAndStatus(UUID usuarioId, String status);
}
