package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.TokenVerificacaoEmail;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.TokenVerificacaoEmailRepository;
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
public class ReenviarEmailUseCase {

    private final UsuarioRepository usuarioRepository;
    private final TokenVerificacaoEmailRepository tokenRepository;
    private final EmailService emailService;

    @Transactional
    public void reenviarVerificacao(String email) {
        Optional<Usuario> optUsuario = usuarioRepository.findByEmailAndAtivoTrue(email);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();

            if (Boolean.FALSE.equals(usuario.getVerificado())) {

                // 🔥 NOVO: Busca todos os tokens antigos e invalida
                List<TokenVerificacaoEmail> tokensAntigos = tokenRepository.findByUsuarioIdAndVerificadoFalse(usuario.getId());
                tokensAntigos.forEach(t -> t.setVerificado(true)); // Marca como usado/inválido
                tokenRepository.saveAll(tokensAntigos);

                // Cria o novo token
                String tokenStr = UUID.randomUUID().toString();
                TokenVerificacaoEmail token = new TokenVerificacaoEmail();
                token.setUsuarioId(usuario.getId());
                token.setToken(tokenStr);
                token.setExpiraEm(OffsetDateTime.now().plusHours(24));
                token.setVerificado(false);

                tokenRepository.save(token);
                emailService.enviarEmailVerificacao(usuario.getEmail(), usuario.getNome(), tokenStr);
            }
        }
    }
}