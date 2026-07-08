package br.com.otica.otica_loja.Repository.Comercial;

import br.com.otica.otica_loja.Entity.Comercial.Cashback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CashbackRepository extends JpaRepository<Cashback, UUID> {

    // Buscar todos os cashbacks de um usuário
    List<Cashback> findByUsuarioId(UUID usuarioId);

    // Buscar cashbacks ativos (saldo > 0 e não expirados)
    @Query("SELECT c FROM Cashback c " +
            "WHERE c.usuarioId = :usuarioId " +
            "AND c.saldo > 0 " +
            "AND c.expiraEm > :now")
    List<Cashback> findActiveCashbacks(@Param("usuarioId") UUID usuarioId,
                                       @Param("now") OffsetDateTime now);

    // Buscar cashbacks expirados
    List<Cashback> findByUsuarioIdAndExpiraEmBefore(UUID usuarioId, java.time.OffsetDateTime now);

    // Buscar cashback específico pelo ID do usuário e ID do cashback
    Optional<Cashback> findByUsuarioIdAndId(UUID usuarioId, UUID id);

    // Verificar se usuário possui algum cashback ativo
    boolean existsByUsuarioIdAndSaldoGreaterThan(UUID usuarioId, java.math.BigDecimal saldoMinimo);
}
