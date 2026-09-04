package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Atendimento.ChatConversa;
import br.com.otica.otica_loja.Entity.Atendimento.ChatMensagem;
import br.com.otica.otica_loja.UseCases.chat.EnviarMensagemUseCase;
import br.com.otica.otica_loja.UseCases.chat.FinalizarConversaUseCase;
import br.com.otica.otica_loja.UseCases.chat.ListarConversasUseCase;
import br.com.otica.otica_loja.UseCases.chat.ListarMensagensUseCase;
import br.com.otica.otica_loja.enums.RemetenteTipo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/chat")
public class AdminChatController {

    @Autowired
    private EnviarMensagemUseCase enviarMensagemUseCase;

    @Autowired
    private FinalizarConversaUseCase finalizarConversaUseCase;

    @Autowired
    private ListarMensagensUseCase listarMensagensUseCase;

    @Autowired
    private ListarConversasUseCase listarConversasUseCase;


    // DTO local para receber os dados da requisição (você pode mover para a sua pasta de DTOs)
    public record AdminEnviarMensagemRequestDTO(
            String mensagem,
            String arquivoPath
    ) {}

    // 1. Responder o usuário (Enviar mensagem na conversa)
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @PostMapping("/{conversaId}/mensagens")
    public ResponseEntity<ChatMensagem> responderUsuario(
            @PathVariable UUID conversaId,
            @RequestBody AdminEnviarMensagemRequestDTO request) {

        // Define o remetente automaticamente como ATENDENTE (ou ADMIN dependendo da sua regra de negócio)
        ChatMensagem novaMensagem = enviarMensagemUseCase.enviarMensagem(
                conversaId,
                RemetenteTipo.ATENDENTE,
                request.mensagem(),
                request.arquivoPath()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(novaMensagem);
    }


    // Novo endpoint para listar as conversas com filtro
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @GetMapping("/conversas")
    public ResponseEntity<List<ChatConversa>> listarConversas(
            @RequestParam(required = false) List<String> status) {

        List<ChatConversa> conversas = listarConversasUseCase.listarConversas(status);
        return ResponseEntity.ok(conversas);
    }
    // 2. Listar todas as mensagens de uma conversa
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @GetMapping("/{conversaId}/mensagens")
    public ResponseEntity<List<ChatMensagem>> listarMensagens(@PathVariable UUID conversaId) {
        List<ChatMensagem> mensagens = listarMensagensUseCase.listarMensagens(conversaId);
        return ResponseEntity.ok(mensagens);
    }

    // 3. Listar apenas mensagens não visualizadas pelo admin/atendente
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @GetMapping("/{conversaId}/mensagens/nao-visualizadas")
    public ResponseEntity<List<ChatMensagem>> listarMensagensNaoVisualizadas(@PathVariable UUID conversaId) {
        List<ChatMensagem> mensagens = listarMensagensUseCase.listarMensagensNaoVisualizadas(conversaId);
        return ResponseEntity.ok(mensagens);
    }

    // 4. Finalizar a conversa
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @PutMapping("/{conversaId}/finalizar")
    public ResponseEntity<ChatConversa> finalizarConversa(@PathVariable UUID conversaId) {
        ChatConversa conversaFinalizada = finalizarConversaUseCase.finalizarConversa(conversaId);
        return ResponseEntity.ok(conversaFinalizada);
    }
}