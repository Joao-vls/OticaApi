package br.com.otica.otica_loja.Repository.Atendimento;

import br.com.otica.otica_loja.Entity.Atendimento.ChatMensagem;
import br.com.otica.otica_loja.Entity.Atendimento.ChatConversa;
import br.com.otica.otica_loja.enums.RemetenteTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMensagemRepository extends JpaRepository<ChatMensagem, UUID> {

    // Buscar todas as mensagens de uma conversa
    List<ChatMensagem> findByConversa(ChatConversa conversa);

    // Buscar mensagens não visualizadas de uma conversa
    List<ChatMensagem> findByConversaAndVisualizadaFalse(ChatConversa conversa);

    // Buscar mensagens por remetente (USUARIO, ATENDENTE, SISTEMA, etc.)
    List<ChatMensagem> findByConversaAndRemetente(ChatConversa conversa, RemetenteTipo remetente);

    // Buscar mensagens que possuem arquivo anexado
    List<ChatMensagem> findByConversaAndArquivoPathIsNotNull(ChatConversa conversa);
}
