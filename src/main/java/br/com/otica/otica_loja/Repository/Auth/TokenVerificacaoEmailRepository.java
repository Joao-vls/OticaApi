package br.com.otica.otica_loja.Repository.Auth;

import br.com.otica.otica_loja.Entity.Auth.TokenVerificacaoEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenVerificacaoEmailRepository extends JpaRepository<TokenVerificacaoEmail, UUID> {

    // Buscar token pelo valor
    Optional<TokenVerificacaoEmail> findByToken(String token);

    // Buscar token pelo usuário
    Optional<TokenVerificacaoEmail> findByUsuarioId(UUID usuarioId);

    // Verificar se existe token válido para um usuário
    boolean existsByUsuarioIdAndVerificadoFalse(UUID usuarioId);

    // Buscar token não verificado e ainda válido
    Optional<TokenVerificacaoEmail> findByTokenAndVerificadoFalseAndExpiraEmAfter(String token, java.time.OffsetDateTime agora);
}
