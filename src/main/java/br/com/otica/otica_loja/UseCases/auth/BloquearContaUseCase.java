package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.TokenDesbloqueio;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.TokenDesbloqueioRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BloquearContaUseCase {

    private final UsuarioRepository usuarioRepository;
    private final TokenDesbloqueioRepository tokenRepository;
    private final EmailService emailService;

    @Transactional
    public void bloquearEEnviarEmail(String email) {
        Optional<Usuario> optUsuario = usuarioRepository.findByEmailAndAtivoTrue(email);
        // Se o usuário existir, repassa para o método principal de bloqueio
        optUsuario.ifPresent(this::processarBloqueioEEnvio);
    }

    @Transactional
    public void processarBloqueioEEnvio(Usuario usuario) {
        usuario.setBloqueado(true);
        usuarioRepository.save(usuario);

        // 🔥 NOVO: Busca e invalida tokens de desbloqueio anteriores
        List<TokenDesbloqueio> tokensAntigos = tokenRepository.findByUsuarioIdAndUtilizadoFalse(usuario.getId());
        tokensAntigos.forEach(t -> t.setUtilizado(true));
        tokenRepository.saveAll(tokensAntigos);

        // Gera o novo token
        String tokenStr = UUID.randomUUID().toString();
        TokenDesbloqueio token = new TokenDesbloqueio();
        token.setUsuarioId(usuario.getId());
        token.setToken(tokenStr);
        token.setExpiraEm(OffsetDateTime.now().plusHours(2));
        token.setUtilizado(false);

        tokenRepository.save(token);
        emailService.enviarEmailDesbloqueio(usuario.getEmail(), usuario.getNome(), tokenStr);
    }
}