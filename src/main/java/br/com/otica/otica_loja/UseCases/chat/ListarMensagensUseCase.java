package br.com.otica.otica_loja.UseCases.chat;

import br.com.otica.otica_loja.Entity.Atendimento.ChatConversa;
import br.com.otica.otica_loja.Entity.Atendimento.ChatMensagem;
import br.com.otica.otica_loja.Repository.Atendimento.ChatConversaRepository;
import br.com.otica.otica_loja.Repository.Atendimento.ChatMensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarMensagensUseCase {

    @Autowired
    private ChatConversaRepository conversaRepository;

    @Autowired
    private ChatMensagemRepository mensagemRepository;

    /**
     * Lista todas as mensagens de uma conversa.
     */
    public List<ChatMensagem> listarMensagens(UUID conversaId) {
        ChatConversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new IllegalArgumentException("Conversa não encontrada."));

        return mensagemRepository.findByConversa(conversa);
    }

    /**
     * Lista apenas mensagens não visualizadas de uma conversa.
     */
    public List<ChatMensagem> listarMensagensNaoVisualizadas(UUID conversaId) {
        ChatConversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new IllegalArgumentException("Conversa não encontrada."));

        return mensagemRepository.findByConversaAndVisualizadaFalse(conversa);
    }

    /**
     * Lista mensagens de um remetente específico (USUARIO, ATENDENTE, SISTEMA).
     */
    public List<ChatMensagem> listarMensagensPorRemetente(UUID conversaId, String remetenteTipo) {
        ChatConversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new IllegalArgumentException("Conversa não encontrada."));

        return mensagemRepository.findByConversaAndRemetente(conversa, Enum.valueOf(
                br.com.otica.otica_loja.enums.RemetenteTipo.class, remetenteTipo.toUpperCase()));
    }

    /**
     * Lista mensagens que possuem arquivos anexados.
     */
    public List<ChatMensagem> listarMensagensComArquivo(UUID conversaId) {
        ChatConversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new IllegalArgumentException("Conversa não encontrada."));

        return mensagemRepository.findByConversaAndArquivoPathIsNotNull(conversa);
    }
}
