package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.TokenDesbloqueio;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.TokenDesbloqueioRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class DesbloquearContaUseCase {

    private final TokenDesbloqueioRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void executar(String tokenStr) {
        TokenDesbloqueio token = tokenRepository.findByTokenAndUtilizadoFalseAndExpiraEmAfter(tokenStr, OffsetDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado. Tente fazer login novamente para receber um novo link."));

        Usuario usuario = usuarioRepository.findById(token.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        usuario.setBloqueado(false);
        usuario.setTentativasFalhas(0);
        usuarioRepository.save(usuario);

        token.setUtilizado(true);
        tokenRepository.save(token);
    }
}