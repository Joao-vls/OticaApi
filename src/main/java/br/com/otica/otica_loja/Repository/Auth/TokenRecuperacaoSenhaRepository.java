package br.com.otica.otica_loja.Repository.Auth;

import br.com.otica.otica_loja.Entity.Auth.TokenRecuperacaoSenha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRecuperacaoSenhaRepository extends JpaRepository<TokenRecuperacaoSenha, UUID> {

    // Buscar token pelo valor
    Optional<TokenRecuperacaoSenha> findByToken(String token);

    // Buscar token válido (não utilizado e não expirado)
    Optional<TokenRecuperacaoSenha> findByTokenAndUtilizadoFalseAndExpiraEmAfter(String token, java.time.OffsetDateTime agora);

    // Buscar token pelo usuário
    Optional<TokenRecuperacaoSenha> findByUsuarioId(UUID usuarioId);

    // Verificar se existe token ativo para o usuário
    boolean existsByUsuarioIdAndUtilizadoFalse(UUID usuarioId);
}
