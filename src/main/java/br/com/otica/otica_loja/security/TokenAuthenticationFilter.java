package br.com.otica.otica_loja.security;

import br.com.otica.otica_loja.Entity.Auth.Sessao;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.SessaoRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = recuperarTokenDosCookies(request);

        if (token != null) {
            Optional<Sessao> sessaoOpt = sessaoRepository.findByTokenAndAtivoTrueAndExpiraEmAfter(token, OffsetDateTime.now());

            if (sessaoOpt.isPresent()) {
                Sessao sessao = sessaoOpt.get();

                // 🔒 VALIDAÇÃO DE SEGURANÇA: User-Agent
                String currentAgent = request.getHeader("User-Agent");

                // Se a sessão tem um User-Agent gravado, valida se o da requisição atual é igual
                if (sessao.getUserAgent() != null && !sessao.getUserAgent().equals(currentAgent)) {
                    // Navegador/Dispositivo diferente! Invalida o contexto e aborta a autenticação.
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                Optional<Usuario> usuarioOpt = usuarioRepository.findById(sessao.getUsuarioId());

                if (usuarioOpt.isPresent() && Boolean.TRUE.equals(usuarioOpt.get().getAtivo())) {
                    Usuario usuario = usuarioOpt.get();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Recupera o token adequado conforme o contexto da requisição (Admin x Cliente).
     */
    private String recuperarTokenDosCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        String path = request.getRequestURI();
        boolean isAdminRoute = path.startsWith("/admin");

        // 1. Se for uma rota /admin/..., prioriza o cookie "admin_token"
        if (isAdminRoute) {
            for (Cookie cookie : request.getCookies()) {
                if ("admin_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        } else {
            // 2. Para rotas de cliente (/api/...), busca "client_token"
            for (Cookie cookie : request.getCookies()) {
                if ("client_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
            // Fallback: Se não achou client_token, tenta admin_token (útil se um admin acessar recursos comuns da API)
            for (Cookie cookie : request.getCookies()) {
                if ("admin_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}