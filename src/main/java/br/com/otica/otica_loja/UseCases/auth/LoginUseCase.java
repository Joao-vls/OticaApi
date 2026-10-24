package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.Sessao;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.SessaoRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UsuarioRepository usuarioRepository;
    private final SessaoRepository sessaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final BloquearContaUseCase bloquearContaUseCase;

    private static final int MAX_TENTATIVAS = 5;
    private static final int SESSAO_EXPIRACAO_HORAS = 4;

    /**
     * Realiza o login do usuário:
     *  - valida usuário ativo
     *  - verifica bloqueio por tentativas
     *  - valida senha
     *  - incrementa tentativas falhas e bloqueia quando necessário
     *  - invalida sessão anterior ativa
     *  - cria e persiste nova sessão
     *
     * @param email       e-mail do usuário
     * @param senhaPlana  senha em texto plano
     * @return Sessao criada e persistida
     */
    @Transactional
    public Sessao login(String email, String senhaPlana) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        // 1. Verifica se conta está bloqueada
        if (Boolean.TRUE.equals(usuario.getBloqueado())) {
            throw new IllegalStateException("Conta bloqueada. Verifique seu e-mail para instruções de desbloqueio.");
        }

        // 2. Valida senha
        if (!passwordEncoder.matches(senhaPlana, usuario.getSenhaHash())) {
            // Incrementa tentativas falhas
            int tentativas = usuario.getTentativasFalhas() == null ? 0 : usuario.getTentativasFalhas();
            tentativas++;
            usuario.setTentativasFalhas(tentativas);

            if (tentativas >= MAX_TENTATIVAS) {
                // Bloqueia a conta e dispara processo de notificação (e-mail)
                bloquearContaUseCase.bloquearEEnviarEmail(usuario.getEmail());
                usuarioRepository.save(usuario);
                throw new IllegalStateException("Muitas tentativas incorretas. Conta bloqueada por segurança.");
            }

            usuarioRepository.save(usuario);
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        // 3. Senha correta: zera tentativas falhas se houver
        if (usuario.getTentativasFalhas() != null && usuario.getTentativasFalhas() > 0) {
            usuario.setTentativasFalhas(0);
            usuarioRepository.save(usuario);
        }

        // 4. Invalida sessão anterior ativa (se existir)
        Optional<Sessao> sessaoAtivaOpt = sessaoRepository.findByUsuarioIdAndAtivoTrue(usuario.getId());
        sessaoAtivaOpt.ifPresent(sessao -> {
            sessao.setAtivo(false);
            sessaoRepository.save(sessao);
        });

        // 5. Cria nova sessão
        Sessao novaSessao = new Sessao();
        novaSessao.setUsuarioId(usuario.getId());
        novaSessao.setToken(UUID.randomUUID().toString());
        novaSessao.setAtivo(true);
        novaSessao.setCriadoEm(OffsetDateTime.now());
        novaSessao.setExpiraEm(OffsetDateTime.now().plusHours(SESSAO_EXPIRACAO_HORAS));

        // 6. Persiste e retorna
        return sessaoRepository.save(novaSessao);
    }
}
