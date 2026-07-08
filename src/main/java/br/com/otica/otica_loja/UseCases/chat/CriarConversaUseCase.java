package br.com.otica.otica_loja.UseCases.chat;

import br.com.otica.otica_loja.Entity.Atendimento.ChatConversa;
import br.com.otica.otica_loja.Entity.Atendimento.ChatMensagem;
import br.com.otica.otica_loja.Repository.Atendimento.ChatConversaRepository;
import br.com.otica.otica_loja.Repository.Atendimento.ChatMensagemRepository;
import br.com.otica.otica_loja.enums.RemetenteTipo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CriarConversaUseCase {

    @Autowired
    private ChatConversaRepository conversaRepository;

    @Autowired
    private ChatMensagemRepository mensagemRepository;

    /**
     * Cria uma nova conversa de atendimento.
     */
    public ChatConversa criarConversa(UUID usuarioId, String sessaoAnonima, String canal) {
        ChatConversa conversa = new ChatConversa();
        conversa.setUsuarioId(usuarioId);
        conversa.setSessaoAnonima(sessaoAnonima);
        conversa.setCanal(canal != null ? canal : "site");
        conversa.setStatus("aberto");
        conversa.setCriadoEm(OffsetDateTime.now());
        conversa.setAtualizadoEm(OffsetDateTime.now());

        return conversaRepository.save(conversa);
    }

    /**
     * Cria uma nova conversa já com a primeira mensagem.
     */
    public ChatConversa criarConversaComMensagem(UUID usuarioId, String sessaoAnonima, String canal,
                                                 RemetenteTipo remetente, String mensagem, String arquivoPath) {
        ChatConversa conversa = criarConversa(usuarioId, sessaoAnonima, canal);

        ChatMensagem chatMensagem = new ChatMensagem();
        chatMensagem.setConversa(conversa);
        chatMensagem.setRemetente(remetente);
        chatMensagem.setMensagem(mensagem);
        chatMensagem.setArquivoPath(arquivoPath);
        chatMensagem.setVisualizada(false);
        chatMensagem.setCriadoEm(OffsetDateTime.now());

        mensagemRepository.save(chatMensagem);

        return conversa;
    }
}
