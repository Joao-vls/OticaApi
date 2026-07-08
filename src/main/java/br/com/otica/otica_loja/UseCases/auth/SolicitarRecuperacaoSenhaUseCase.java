package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.TokenRecuperacaoSenha;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.TokenRecuperacaoSenhaRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SolicitarRecuperacaoSenhaUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenRecuperacaoSenhaRepository tokenRepository;

    /**
     * Solicita a recuperação de senha para um usuário pelo email.
     * Gera um token único e salva no banco.
     */
    public TokenRecuperacaoSenha solicitar(String email) {
        // 1. Buscar usuário ativo pelo email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndAtivoTrue(email);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado ou inativo.");
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Verificar se já existe token ativo para o usuário
        boolean existeTokenAtivo = tokenRepository.existsByUsuarioIdAndUtilizadoFalse(usuario.getId());
        if (existeTokenAtivo) {
            throw new IllegalStateException("Já existe um token ativo para este usuário.");
        }

        // 3. Gerar novo token
        String tokenGerado = UUID.randomUUID().toString();

        TokenRecuperacaoSenha token = new TokenRecuperacaoSenha();
        token.setUsuarioId(usuario.getId());
        token.setToken(tokenGerado);
        token.setExpiraEm(OffsetDateTime.now().plusHours(2)); // expira em 2 horas
        token.setUtilizado(false);
        token.setCriadoEm(OffsetDateTime.now());

        // 4. Persistir e retornar
        return tokenRepository.save(token);
    }
}
