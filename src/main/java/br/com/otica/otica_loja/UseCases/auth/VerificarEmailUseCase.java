package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.TokenVerificacaoEmail;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.TokenVerificacaoEmailRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class VerificarEmailUseCase {

    private final TokenVerificacaoEmailRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Verifica o e-mail do usuário a partir de um token.
     * Regras aplicadas:
     *  - token deve existir
     *  - token não pode estar marcado como verificado
     *  - token não pode estar expirado
     *  - marca token como verificado e marca usuário como verificado
     *
     * @param tokenStr token recebido por e-mail
     * @return usuário verificado
     */
    @Transactional
    public Usuario verificar(String tokenStr) {
        TokenVerificacaoEmail token = tokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou não encontrado."));

        if (Boolean.TRUE.equals(token.getVerificado())) {
            throw new IllegalArgumentException("Este e-mail já foi verificado anteriormente.");
        }

        if (token.getExpiraEm() == null || token.getExpiraEm().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("O token expirou. Solicite um novo reenvio.");
        }

        // Marcar token como verificado
        token.setVerificado(true);
        tokenRepository.save(token);

        // Atualizar usuário para verificado
        Usuario usuario = usuarioRepository.findById(token.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para este token."));

        if (Boolean.TRUE.equals(usuario.getVerificado())) {
            // Caso raro: usuário já marcado como verificado, apenas retorna
            return usuario;
        }

        usuario.setVerificado(true);
        usuarioRepository.save(usuario);

        return usuario;
    }
}
