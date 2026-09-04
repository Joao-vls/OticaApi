package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Atendimento.ChatConversa;
import br.com.otica.otica_loja.Entity.Atendimento.ChatMensagem;
import br.com.otica.otica_loja.UseCases.chat.CriarConversaUseCase;
import br.com.otica.otica_loja.UseCases.chat.EnviarMensagemUseCase;
import br.com.otica.otica_loja.UseCases.chat.FinalizarConversaUseCase;
import br.com.otica.otica_loja.UseCases.chat.ListarMensagensUseCase;
import br.com.otica.otica_loja.enums.RemetenteTipo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cliente/chat")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENTE')") // 🔒 Segurança: Protege todos os endpoints, eliminando verificações manuais de null
public class ClienteChatController {

    private final CriarConversaUseCase criarConversaUseCase;
    private final EnviarMensagemUseCase enviarMensagemUseCase;
    private final ListarMensagensUseCase listarMensagensUseCase;
    private final FinalizarConversaUseCase finalizarConversaUseCase;

    public record IniciarConversaDTO(String mensagem, String arquivoPath, String canal) {}
    public record EnviarMensagemDTO(String mensagem, String arquivoPath) {}

    @PostMapping
    public ResponseEntity<ChatConversa> iniciarConversa(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody(required = false) IniciarConversaDTO dto) {

        ChatConversa conversa;
        String canal = (dto != null && dto.canal() != null) ? dto.canal() : "site";

        if (dto != null && dto.mensagem() != null && !dto.mensagem().isBlank()) {
            conversa = criarConversaUseCase.criarConversaComMensagem(
                    usuarioLogado.getId(), // 🔒 ID seguro injetado pelo token
                    null,
                    canal,
                    RemetenteTipo.CLIENTE, // 🔒 Força o remetente, ignorando qualquer manipulação externa
                    dto.mensagem(),
                    dto.arquivoPath()
            );
        } else {
            conversa = criarConversaUseCase.criarConversa(
                    usuarioLogado.getId(), // 🔒 ID seguro
                    null,
                    canal
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(conversa);
    }

    @PostMapping("/{conversaId}/mensagens")
    public ResponseEntity<ChatMensagem> enviarMensagem(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable UUID conversaId,
            @RequestBody EnviarMensagemDTO dto) {

        // Idealmente, seu EnviarMensagemUseCase deve verificar se o conversaId pertence ao usuarioLogado.getId()
        ChatMensagem novaMensagem = enviarMensagemUseCase.enviarMensagem(
                conversaId,
                RemetenteTipo.CLIENTE, // 🔒 Segurança mantida: O backend define quem está enviando
                dto.mensagem(),
                dto.arquivoPath()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(novaMensagem);
    }

    @GetMapping("/{conversaId}/mensagens")
    public ResponseEntity<List<ChatMensagem>> listarMensagens(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable UUID conversaId) {

        List<ChatMensagem> mensagens = listarMensagensUseCase.listarMensagens(conversaId);

        // 🔒 Segurança: Garante que um cliente não possa ler o chat de outro cliente via requisição GET
        if (!mensagens.isEmpty()) {
            UUID donoDaConversa = mensagens.get(0).getConversa().getUsuarioId();
            if (donoDaConversa != null && !donoDaConversa.equals(usuarioLogado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
            }
        }

        return ResponseEntity.ok(mensagens);
    }

    @PostMapping("/{conversaId}/finalizar")
    public ResponseEntity<ChatConversa> finalizarConversa(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable UUID conversaId) {

        // Assim como no envio de mensagens, certifique-se de que o caso de uso ou uma validação aqui
        // garanta que apenas o dono (usuarioLogado.getId()) ou um ADMIN possa finalizar a conversa.
        ChatConversa conversaFinalizada = finalizarConversaUseCase.finalizarConversa(conversaId);

        return ResponseEntity.ok(conversaFinalizada);
    }
}