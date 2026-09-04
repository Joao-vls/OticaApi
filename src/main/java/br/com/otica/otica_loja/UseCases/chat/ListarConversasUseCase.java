package br.com.otica.otica_loja.UseCases.chat;

import br.com.otica.otica_loja.Entity.Atendimento.ChatConversa;
import br.com.otica.otica_loja.Repository.Atendimento.ChatConversaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarConversasUseCase {

    @Autowired
    private ChatConversaRepository conversaRepository;

    public List<ChatConversa> listarConversas(List<String> status) {
        // Se não passar status nenhum, retorna todas ordenadas pelas mais recentes
        if (status == null || status.isEmpty()) {
            return conversaRepository.findAllByOrderByAtualizadoEmDesc();
        }

        // Retorna as conversas com os status selecionados, ordenadas pelas mais recentes
        return conversaRepository.findByStatusInOrderByAtualizadoEmDesc(status);
    }
}