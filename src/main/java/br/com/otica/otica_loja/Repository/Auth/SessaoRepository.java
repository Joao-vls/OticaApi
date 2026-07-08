package br.com.otica.otica_loja.Repository.Auth;

import br.com.otica.otica_loja.Entity.Auth.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, UUID> {

    // Buscar sessão pelo token
    Optional<Sessao> findByToken(String token);

    // Buscar sessões ativas de um usuário
    Optional<Sessao> findByUsuarioIdAndAtivoTrue(UUID usuarioId);

    // Verificar se existe sessão ativa para o usuário
    boolean existsByUsuarioIdAndAtivoTrue(UUID usuarioId);

    // Buscar sessão válida (ativa e não expirada)
    Optional<Sessao> findByTokenAndAtivoTrueAndExpiraEmAfter(String token, java.time.OffsetDateTime agora);
}
