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
public class EnviarMensagemUseCase {

    @Autowired
    private ChatConversaRepository conversaRepository;

    @Autowired
    private ChatMensagemRepository mensagemRepository;

    /**
     * Envia uma nova mensagem em uma conversa existente.
     */
    public ChatMensagem enviarMensagem(UUID conversaId, RemetenteTipo remetente, String mensagem, String arquivoPath) {
        // 1. Buscar conversa
        ChatConversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new IllegalArgumentException("Conversa não encontrada."));

        if ("encerrado".equalsIgnoreCase(conversa.getStatus())) {
            throw new IllegalStateException("Não é possível enviar mensagens em uma conversa encerrada.");
        }

        // 2. Criar mensagem
        ChatMensagem chatMensagem = new ChatMensagem();
        chatMensagem.setConversa(conversa);
        chatMensagem.setRemetente(remetente);
        chatMensagem.setMensagem(mensagem);
        chatMensagem.setArquivoPath(arquivoPath);
        chatMensagem.setVisualizada(false);
        chatMensagem.setCriadoEm(OffsetDateTime.now());

        // 3. Atualizar conversa
        conversa.setAtualizadoEm(OffsetDateTime.now());
        if ("aberto".equalsIgnoreCase(conversa.getStatus())) {
            conversa.setStatus("em_atendimento");
        }
        conversaRepository.save(conversa);

        // 4. Persistir mensagem
        return mensagemRepository.save(chatMensagem);
    }
}
