package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.TokenRecuperacaoSenha;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.TokenRecuperacaoSenhaRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class RedefinirSenhaUseCase {

    @Autowired
    private TokenRecuperacaoSenhaRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Redefine a senha do usuário a partir de um token válido.
     */
    public Usuario redefinirSenha(String token, String novaSenha) {
        // 1. Buscar token válido (não utilizado e não expirado)
        Optional<TokenRecuperacaoSenha> tokenOpt =
                tokenRepository.findByTokenAndUtilizadoFalseAndExpiraEmAfter(token, OffsetDateTime.now());

        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        TokenRecuperacaoSenha tokenRecuperacao = tokenOpt.get();

        // 2. Buscar usuário associado
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(tokenRecuperacao.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            throw new IllegalStateException("Usuário não encontrado para este token.");
        }

        Usuario usuario = usuarioOpt.get();

        // 3. Atualizar senha do usuário (com hash seguro)
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        // 4. Marcar token como utilizado
        tokenRecuperacao.setUtilizado(true);
        tokenRepository.save(tokenRecuperacao);

        return usuario;
    }
}
