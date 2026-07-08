package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.Sessao;
import br.com.otica.otica_loja.Repository.Auth.SessaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenUseCase {

    @Autowired
    private SessaoRepository sessaoRepository;

    /**
     * Atualiza o token de sessão se ele ainda estiver válido.
     */
    public Sessao refresh(String tokenAntigo) {
        // 1. Buscar sessão válida (ativa e não expirada)
        Optional<Sessao> sessaoOpt = sessaoRepository.findByTokenAndAtivoTrueAndExpiraEmAfter(tokenAntigo, OffsetDateTime.now());
        if (sessaoOpt.isEmpty()) {
            throw new IllegalArgumentException("Sessão inválida ou expirada.");
        }

        Sessao sessaoAntiga = sessaoOpt.get();

        // 2. Invalidar sessão antiga
        sessaoAntiga.setAtivo(false);
        sessaoRepository.save(sessaoAntiga);

        // 3. Criar nova sessão com novo token
        Sessao novaSessao = new Sessao();
        novaSessao.setUsuarioId(sessaoAntiga.getUsuarioId());
        novaSessao.setToken(UUID.randomUUID().toString());
        novaSessao.setExpiraEm(OffsetDateTime.now().plusHours(4)); // expira em 4 horas
        novaSessao.setAtivo(true);
        novaSessao.setCriadoEm(OffsetDateTime.now());

        // 4. Persistir e retornar
        return sessaoRepository.save(novaSessao);
    }
}
