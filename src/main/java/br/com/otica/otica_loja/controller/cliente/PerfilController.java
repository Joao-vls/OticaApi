package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Auth.Perfil;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.EnderecoRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.UseCases.usuario.*;

import br.com.otica.otica_loja.dto.AlterarEmailDTO;
import br.com.otica.otica_loja.dto.AlterarTelefoneDTO;
import br.com.otica.otica_loja.dto.AtualizarPerfilDTO;
import br.com.otica.otica_loja.dto.EnderecoDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cliente/perfil")
public class PerfilController {

    @Autowired
    private AtualizarPerfilUseCase atualizarPerfilUseCase;

    @Autowired
    private AdicionarEnderecoUseCase adicionarEnderecoUseCase;

    @Autowired
    private AtualizarEnderecoUseCase atualizarEnderecoUseCase;

    @Autowired
    private AlterarEmailUseCase alterarEmailUseCase;

    @Autowired
    private AlterarTelefoneUseCase alterarTelefoneUseCase;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    /**
     * Retorna os dados completos do perfil do usuário logado.
     */
    @GetMapping
    public ResponseEntity<?> obterPerfilCompleto(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioRepository.findById(usuarioLogado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        Perfil perfil = usuario.getPerfil();
        List<Endereco> enderecos = enderecoRepository.findByUsuarioId(usuario.getId());

        return ResponseEntity.ok(Map.of(
                "id", usuario.getId(),
                "nome", usuario.getNome(),
                "email", usuario.getEmail(),
                "telefone", usuario.getTelefone(),
                "perfil", perfil != null ? perfil : Map.of(),
                "enderecos", enderecos
        ));
    }

    /**
     * Atualiza as informações pessoais do cliente.
     */
    @PutMapping
    public ResponseEntity<?> atualizarPerfil(@AuthenticationPrincipal Usuario usuarioLogado,
                                             @Valid @RequestBody AtualizarPerfilDTO dto) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // Tratamento para converter dataNascimento com segurança contra null/vazio
            LocalDate dataNascimento = (dto.dataNascimento() != null && !dto.dataNascimento().isBlank())
                    ? LocalDate.parse(dto.dataNascimento())
                    : null;

            Perfil perfilAtualizado = atualizarPerfilUseCase.atualizar(
                    usuarioLogado.getId(),
                    dto.nome(),
                    dto.telefone(),
                    dto.username(),
                    dataNascimento,
                    dto.cpf(),
                    dto.genero()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Perfil atualizado com sucesso!",
                    "perfil", perfilAtualizado
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao atualizar perfil: " + e.getMessage()));
        }
    }
    /**
     * Lista todos os endereços cadastrados do usuário logado.
     */
    @GetMapping("/enderecos")
    public ResponseEntity<?> listarEnderecos(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Endereco> enderecos = enderecoRepository.findByUsuarioId(usuarioLogado.getId());
        return ResponseEntity.ok(enderecos);
    }

    /**
     * Adiciona um novo endereço para o cliente logado.
     */
    @PostMapping("/enderecos")
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
     */
    @PutMapping("/enderecos/{enderecoId}")
    public ResponseEntity<?> atualizarEndereco(@AuthenticationPrincipal Usuario usuarioLogado,
                                               @PathVariable UUID enderecoId,
                                               @Valid @RequestBody EnderecoDTO dto) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 🔒 Segurança extra: Garante que o endereço pertence ao usuário autenticado
        Endereco enderecoOriginal = enderecoRepository.findById(enderecoId).orElse(null);

        if (enderecoOriginal == null || !enderecoOriginal.getUsuarioId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Endereço não pertence ao usuário autenticado ou não existe."));
        }

        try {
            Endereco enderecoAtualizado = atualizarEnderecoUseCase.atualizar(
                    enderecoId,
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
    // Adicione as injeções no PerfilController:


    /**
     * Altera o e-mail do cliente logado.
     */
    @PatchMapping("/email")
    public ResponseEntity<?> alterarEmail(@AuthenticationPrincipal Usuario usuarioLogado,
                                          @Valid @RequestBody AlterarEmailDTO dto) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Usuario usuarioAtualizado = alterarEmailUseCase.alterarEmail(
                    usuarioLogado.getId(),
                    dto.novoEmail()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "E-mail alterado com sucesso!",
                    "email", usuarioAtualizado.getEmail()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao alterar e-mail: " + e.getMessage()));
        }
    }

    /**
     * Altera o número de telefone do cliente logado.
     */
    @PatchMapping("/telefone")
    public ResponseEntity<?> alterarTelefone(@AuthenticationPrincipal Usuario usuarioLogado,
                                             @Valid @RequestBody AlterarTelefoneDTO dto) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Perfil perfilAtualizado = alterarTelefoneUseCase.alterarTelefone(
                    usuarioLogado.getId(),
                    dto.novoTelefone()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Telefone alterado com sucesso!",
                    "telefone", perfilAtualizado.getTelefone()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao alterar telefone: " + e.getMessage()));
        }
    }
}