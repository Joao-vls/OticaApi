package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.Entity.Auth.Sessao;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.SessaoRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.UseCases.auth.*;
import br.com.otica.otica_loja.dto.auth.LoginRequest;
import br.com.otica.otica_loja.dto.auth.RegisterRequest;
import br.com.otica.otica_loja.service.admin.LogAcessoService;
import br.com.otica.otica_loja.service.auth.RateLimitService;
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
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private LoginUseCase loginUseCase;

    @Autowired
    private RegisterUseCase registerUseCase;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private LogAcessoService logAcessoService;

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private VerificarEmailUseCase verificarEmailUseCase;

    @Autowired
    private DesbloquearContaUseCase desbloquearContaUseCase;

    @Autowired
    private ReenviarEmailUseCase reenviarEmailUseCase;

    @Autowired
    private BloquearContaUseCase bloquearContaUseCase;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {

        String ip = rateLimitService.obterIpCliente(httpRequest);
        if (!rateLimitService.resolveRegisterBucket(ip).tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Muitas tentativas de cadastro. Tente novamente mais tarde."));
        }

        try {
            Usuario usuarioCriado = registerUseCase.registrarCliente(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Cliente cadastrado com sucesso!",
                    "email", usuarioCriado.getEmail(),
                    "nome", usuarioCriado.getNome()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro interno ao cadastrar cliente: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        String ip = rateLimitService.obterIpCliente(httpRequest);

        // 1. Defesa de Infraestrutura: Rate Limit por IP
        if (!rateLimitService.resolveLoginBucket(ip).tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Muitas requisições deste dispositivo. Aguarde alguns minutos e tente novamente."));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndAtivoTrue(request.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Credenciais inválidas."));
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Trava e envia/reenvia o e-mail com instruções de desbloqueio se a conta estiver bloqueada
        if (Boolean.TRUE.equals(usuario.getBloqueado())) {
            bloquearContaUseCase.processarBloqueioEEnvio(usuario);

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Sua conta está bloqueada. Reenviamos um e-mail com as instruções de desbloqueio."));
        }

        // 3. Trava se o e-mail não foi verificado
        if (Boolean.FALSE.equals(usuario.getVerificado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Por favor, verifique seu e-mail antes de fazer login."));
        }

        // 4. Evita que Administradores façam login pela área de Cliente
        List<String> rawAuthorities = usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(String::toUpperCase)
                .toList();

        boolean isAdminOuGerente = rawAuthorities.stream().anyMatch(auth ->
                auth.contains("ADMIN") || auth.contains("GERENTE")
        );

        if (isAdminOuGerente) {
            logAcessoService.registrar(httpRequest, usuario.getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Erro de autenticação."));
        }

        try {
            // 5. Executa o login e valida a senha
            Sessao sessao = loginUseCase.login(request.getEmail(), request.getSenha());

            sessao.setUserAgent(httpRequest.getHeader("User-Agent"));
            sessao.setIpAddress(ip);
            sessaoRepository.save(sessao);

            logAcessoService.registrar(httpRequest, usuario.getId());

            ResponseCookie authCookie = ResponseCookie.from("client_token", sessao.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofHours(12)) // Sessão configurada para 12h
                    .sameSite("None")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                    .body(Map.of("message", "Login realizado com sucesso."));

        } catch (IllegalStateException e) {
            logAcessoService.registrar(httpRequest, usuario.getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));

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
    public ResponseEntity<?> obterClienteAutenticado() {
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
                if ("client_token".equals(cookie.getName())) {
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

        ResponseCookie cleanCookie = ResponseCookie.from("client_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(Map.of("message", "Logout realizado com sucesso!"));
    }

    @GetMapping("/verificar-email")
    public ResponseEntity<?> verificarEmail(@RequestParam String token) {
        try {
            verificarEmailUseCase.verificar(token);
            return ResponseEntity.ok(Map.of("message", "E-mail verificado com sucesso! Você já pode fazer login."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao verificar e-mail."));
        }
    }

    @GetMapping("/desbloquear-conta")
    public ResponseEntity<?> desbloquearConta(@RequestParam String token) {
        try {
            desbloquearContaUseCase.executar(token);
            return ResponseEntity.ok(Map.of("message", "Conta desbloqueada com sucesso! Você já pode fazer login."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao desbloquear a conta."));
        }
    }

    @PostMapping("/reenviar-verificacao")
    public ResponseEntity<?> reenviarVerificacao(@RequestBody Map<String, String> body, HttpServletRequest httpRequest) {
        String email = body.get("email");
        String ip = rateLimitService.obterIpCliente(httpRequest);

        // 1. Defesa de Infraestrutura por IP
        if (!rateLimitService.resolveRegisterBucket(ip).tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Muitas tentativas de envio. Aguarde alguns minutos antes de tentar novamente."));
        }

        // 2. Defesa de Regra de Negócio (Bloqueio Exponencial por E-mail)
        long tempoEspera = rateLimitService.verificarRateLimitExponencial("verify_" + email);
        if (tempoEspera > 0) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Por segurança, aguarde " + tempoEspera + " segundos antes de solicitar um novo e-mail."));
        }

        try {
            reenviarEmailUseCase.reenviarVerificacao(email);
            return ResponseEntity.ok(Map.of("message", "Se o e-mail existir, um novo link de ativação foi enviado."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro interno ao processar o reenvio."));
        }
    }

    @PostMapping("/reenviar-desbloqueio")
    public ResponseEntity<?> reenviarDesbloqueio(@RequestBody Map<String, String> body, HttpServletRequest httpRequest) {
        String email = body.get("email");
        String ip = rateLimitService.obterIpCliente(httpRequest);

        // 1. Defesa de Infraestrutura por IP
        if (!rateLimitService.resolveRegisterBucket(ip).tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Muitas tentativas de envio. Aguarde alguns minutos."));
        }

        // 2. Defesa de Regra de Negócio (Bloqueio Exponencial por E-mail)
        long tempoEspera = rateLimitService.verificarRateLimitExponencial("unlock_" + email);
        if (tempoEspera > 0) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Por segurança, aguarde " + tempoEspera + " segundos antes de solicitar um novo e-mail."));
        }

        try {
            bloquearContaUseCase.bloquearEEnviarEmail(email);
            return ResponseEntity.ok(Map.of("message", "Se a conta estiver bloqueada, um novo link de desbloqueio foi enviado."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro interno ao processar o reenvio."));
        }
    }
}