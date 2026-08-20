package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.EnderecoRepository;
import br.com.otica.otica_loja.UseCases.usuario.AdicionarEnderecoUseCase;
import br.com.otica.otica_loja.UseCases.usuario.AtualizarEnderecoUseCase;
import br.com.otica.otica_loja.UseCases.usuario.RemoverEnderecoUseCase;
import br.com.otica.otica_loja.dto.EnderecoDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cliente/enderecos")
public class EnderecoClienteController {

    @Autowired
    private AdicionarEnderecoUseCase adicionarEnderecoUseCase;

    @Autowired
    private AtualizarEnderecoUseCase atualizarEnderecoUseCase;

    @Autowired
    private RemoverEnderecoUseCase removerEnderecoUseCase;

    @Autowired
    private EnderecoRepository enderecoRepository;

    /**
     * Lista todos os endereços cadastrados do usuário logado.
     * GET /api/cliente/enderecos
     */
    @GetMapping
    public ResponseEntity<?> listarEnderecos(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Endereco> enderecos = enderecoRepository.findByUsuarioId(usuarioLogado.getId());
        return ResponseEntity.ok(enderecos);
    }

    /**
     * Adiciona um novo endereço para o cliente logado.
     * POST /api/cliente/enderecos
     */
    @PostMapping
    public ResponseEntity<?> adicionarEndereco(@AuthenticationPrincipal Usuario usuarioLogado,
                                               @Valid @RequestBody EnderecoDTO dto) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Endereco novoEndereco = adicionarEnderecoUseCase.adicionar(
                    usuarioLogado.getId(),
                    dto.nomeRecebedor(),
                    dto.telefoneRecebedor(),
                    dto.cep(),
                    dto.logradouro(),
                    dto.numero(),
                    dto.complemento(),
                    dto.bairro(),
                    dto.cidade(),
                    dto.estado(),
                    dto.pais(),
                    Boolean.TRUE.equals(dto.isDefault())
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(novoEndereco);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Erro ao cadastrar endereço: " + e.getMessage()));
        }
    }

    /**
     * Atualiza um endereço do cliente garantindo o vínculo de propriedade.
     * PUT /api/cliente/enderecos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarEndereco(@AuthenticationPrincipal Usuario usuarioLogado,
                                               @PathVariable UUID id,
                                               @Valid @RequestBody EnderecoDTO dto) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 🔒 Segurança: Valida se o endereço pertence ao usuário autenticado
        Endereco enderecoOriginal = enderecoRepository.findById(id).orElse(null);

        if (enderecoOriginal == null || !enderecoOriginal.getUsuarioId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Endereço não pertence ao usuário autenticado ou não existe."));
        }

        try {
            Endereco enderecoAtualizado = atualizarEnderecoUseCase.atualizar(
                    id,
                    dto.nomeRecebedor(),
                    dto.telefoneRecebedor(),
                    dto.cep(),
                    dto.logradouro(),
                    dto.numero(),
                    dto.complemento(),
                    dto.bairro(),
                    dto.cidade(),
                    dto.estado(),
                    dto.pais(),
                    dto.isDefault()
            );

            return ResponseEntity.ok(enderecoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao atualizar endereço: " + e.getMessage()));
        }
    }

    /**
     * Remove um endereço pertencente ao cliente logado.
     * DELETE /api/cliente/enderecos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerEndereco(@AuthenticationPrincipal Usuario usuarioLogado,
                                             @PathVariable UUID id) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 🔒 Segurança: Valida se o endereço pertence ao usuário autenticado
        Endereco endereco = enderecoRepository.findById(id).orElse(null);

        if (endereco == null || !endereco.getUsuarioId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Endereço não pertence ao usuário autenticado ou não existe."));
        }

        try {
            removerEnderecoUseCase.remover(id);
            return ResponseEntity.ok(Map.of("message", "Endereço removido com sucesso!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao remover endereço: " + e.getMessage()));
        }
    }
}