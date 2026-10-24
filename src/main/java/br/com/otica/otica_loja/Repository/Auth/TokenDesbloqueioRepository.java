package br.com.otica.otica_loja.Repository.Auth;

import br.com.otica.otica_loja.Entity.Auth.TokenDesbloqueio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenDesbloqueioRepository extends JpaRepository<TokenDesbloqueio, UUID> {
    Optional<TokenDesbloqueio> findByTokenAndUtilizadoFalseAndExpiraEmAfter(String token, OffsetDateTime now);
}