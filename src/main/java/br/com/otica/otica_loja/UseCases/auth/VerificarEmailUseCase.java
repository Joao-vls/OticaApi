package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.TokenVerificacaoEmail;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.TokenVerificacaoEmailRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class VerificarEmailUseCase {

    @Autowired
    private TokenVerificacaoEmailRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Verifica o email do usuário a partir de um token válido.
     */
    public Usuario verificar(String token) {
        // 1. Buscar token válido (não verificado e não expirado)
        Optional<TokenVerificacaoEmail> tokenOpt =
                tokenRepository.findByTokenAndVerificadoFalseAndExpiraEmAfter(token, OffsetDateTime.now());

        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        TokenVerificacaoEmail tokenEmail = tokenOpt.get();

        // 2. Buscar usuário associado
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(tokenEmail.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            throw new IllegalStateException("Usuário não encontrado para este token.");
        }

        Usuario usuario = usuarioOpt.get();

        // 3. Marcar usuário como verificado
        usuario.setVerificado(true);
        usuarioRepository.save(usuario);

        // 4. Marcar token como verificado
        tokenEmail.setVerificado(true);
        tokenRepository.save(tokenEmail);

        return usuario;
    }
}
