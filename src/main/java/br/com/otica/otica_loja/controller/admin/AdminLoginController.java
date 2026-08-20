package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Auth.Sessao;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.SessaoRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.UseCases.auth.LoginUseCase;
import br.com.otica.otica_loja.dto.auth.LoginRequest;
import br.com.otica.otica_loja.service.admin.LogAcessoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/admin/auth")
public class AdminLoginController {

    @Autowired
    private LoginUseCase loginUseCase;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private LogAcessoService logAcessoService;

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndAtivoTrue(request.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Credenciais inválidas."));
        }

        Usuario usuario = usuarioOpt.get();

        // 1. Validação prévia de permissão (Garante perfil ADMIN/GERENTE antes de tentar o login/senha)
        List<String> rawAuthorities = usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(String::toUpperCase)
                .toList();

        boolean isAdminOuGerente = rawAuthorities.stream().anyMatch(auth ->
                auth.contains("ADMIN") || auth.contains("GERENTE")
        );

        if (!isAdminOuGerente) {
            logAcessoService.registrar(httpRequest, usuario.getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Acesso negado: Este painel é restrito a administradores e gerentes."));
        }

        try {
            // 2. Executa a autenticação e gera a sessão apenas para usuários autorizados
            Sessao sessao = loginUseCase.login(request.getEmail(), request.getSenha());

            sessao.setUserAgent(httpRequest.getHeader("User-Agent"));
            sessao.setIpAddress(httpRequest.getRemoteAddr());
            sessaoRepository.save(sessao);

            logAcessoService.registrar(httpRequest, usuario.getId());

            ResponseCookie authCookie = ResponseCookie.from("admin_token", sessao.getToken())
                    .httpOnly(true)
                    .secure(false) // Mudar para true em produção com HTTPS
                    .path("/")
                    .maxAge(Duration.ofHours(1))
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                    .body(Map.of("message", "Autenticado com sucesso."));

        } catch (IllegalArgumentException e) {
            logAcessoService.registrar(httpRequest, usuario.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));

        } catch (Exception e) {
            logAcessoService.registrar(httpRequest, usuario.getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro interno no servidor."));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();

        List<String> roles = usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(role -> role.replace("ROLE_", ""))
                .toList();

        return ResponseEntity.ok(Map.of(
                "id", usuario.getId(),
                "nome", usuario.getNome(),
                "email", usuario.getEmail(),
                "roles", roles
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("admin_token".equals(cookie.getName())) {
                    sessaoRepository.findByTokenAndAtivoTrueAndExpiraEmAfter(cookie.getValue(), OffsetDateTime.now())
                            .ifPresent(sessao -> {
                                sessao.setAtivo(false);
                                sessaoRepository.save(sessao);
                            });
                }
            }
        }

        SecurityContextHolder.clearContext();

        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        ResponseCookie cleanCookie = ResponseCookie.from("admin_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(Map.of("message", "Logout realizado com sucesso!"));
    }
}