package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Auth.Sessao;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.UseCases.auth.LoginUseCase;
import br.com.otica.otica_loja.dto.auth.LoginRequest;
import br.com.otica.otica_loja.dto.auth.LoginResponse;
import br.com.otica.otica_loja.enums.PermissaoNome;
import br.com.otica.otica_loja.service.admin.LogAcessoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin/auth")
@CrossOrigin(origins = "http://192.168.1.100:4200")
public class AdminLoginController {

    @Autowired
    private LoginUseCase loginUseCase;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogAcessoService logAcessoService;

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndAtivoTrue(request.getEmail());

        try {
            if (usuarioOpt.isEmpty()) {
                logAcessoService.registrar(httpRequest, null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
            }

            Usuario usuario = usuarioOpt.get();

            // 👈 Linha corrigida aqui:
            boolean isAdminOuGerente = usuario.getPermissoes().stream()
                    .anyMatch(p -> p.getNome() == PermissaoNome.ADMIN || p.getNome() == PermissaoNome.GERENTE);

            if (!isAdminOuGerente) {
                logAcessoService.registrar(httpRequest, usuario.getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado: Este painel é restrito a administradores.");
            }

            // Executa o fluxo de login do UseCase
            Sessao sessao = loginUseCase.login(request.getEmail(), request.getSenha());

            // Registra o log de sucesso
            logAcessoService.registrar(httpRequest, usuario.getId());

            LoginResponse response = new LoginResponse(sessao.getToken(), "Bearer", sessao.getExpiraEm());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            usuarioOpt.ifPresent(u -> logAcessoService.registrar(httpRequest, u.getId()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());

        } catch (Exception e) {
            usuarioOpt.ifPresent(u -> logAcessoService.registrar(httpRequest, u.getId()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor.");
        }
    }
}