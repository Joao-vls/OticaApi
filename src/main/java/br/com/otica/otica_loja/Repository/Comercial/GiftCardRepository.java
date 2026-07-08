package br.com.otica.otica_loja.Repository.Comercial;

import br.com.otica.otica_loja.Entity.Comercial.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, UUID> {

    // Buscar gift card pelo código
    Optional<GiftCard> findByCodigo(String codigo);

    // Verificar se já existe gift card com determinado código
    boolean existsByCodigo(String codigo);

    // Buscar todos os gift cards ativos
    List<GiftCard> findByAtivoTrue();

    // Buscar todos os gift cards inativos
    List<GiftCard> findByAtivoFalse();

    // Buscar gift cards válidos (ativos, saldo > 0 e não expirados)
    List<GiftCard> findByAtivoTrueAndSaldoGreaterThanAndExpiraEmAfter(java.math.BigDecimal saldoMinimo, OffsetDateTime now);

    // Buscar gift cards expirados
    List<GiftCard> findByExpiraEmBefore(OffsetDateTime now);
}
