package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Auth.Sessao;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Auth.SessaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoginUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Realiza login do usuário e cria uma nova sessão.
     */
    public Sessao login(String email, String senha) {
        // 1. Buscar usuário ativo pelo email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndAtivoTrue(email);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado ou inativo.");
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Validar senha
        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw new IllegalArgumentException("Senha inválida.");
        }

        // 3. Invalidar sessão anterior ativa (se existir)
        Optional<Sessao> sessaoAtiva = sessaoRepository.findByUsuarioIdAndAtivoTrue(usuario.getId());
        sessaoAtiva.ifPresent(sessao -> {
            sessao.setAtivo(false);
            sessaoRepository.save(sessao);
        });

        // 4. Criar nova sessão
        Sessao novaSessao = new Sessao();
        novaSessao.setUsuarioId(usuario.getId());
        novaSessao.setToken(UUID.randomUUID().toString());
        novaSessao.setExpiraEm(OffsetDateTime.now().plusHours(4)); // expira em 4 horas
        novaSessao.setAtivo(true);
        novaSessao.setCriadoEm(OffsetDateTime.now());

        // 5. Persistir e retornar
        return sessaoRepository.save(novaSessao);
    }
}
