package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.Sessao;
import br.com.otica.otica_loja.Repository.Auth.SessaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LogoutUseCase {

    @Autowired
    private SessaoRepository sessaoRepository;

    /**
     * Invalida a sessão ativa de um usuário pelo token.
     */
    public void logout(String token) {
        // 1. Buscar sessão pelo token
        Optional<Sessao> sessaoOpt = sessaoRepository.findByToken(token);

        if (sessaoOpt.isEmpty()) {
            throw new IllegalArgumentException("Sessão não encontrada.");
        }

        Sessao sessao = sessaoOpt.get();

        // 2. Verificar se já está inativa
        if (!sessao.getAtivo()) {
            throw new IllegalStateException("Sessão já está inativa.");
        }

        // 3. Invalidar sessão
        sessao.setAtivo(false);
        sessaoRepository.save(sessao);
    }
}
