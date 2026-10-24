package br.com.otica.otica_loja.Repository.CRM;

import br.com.otica.otica_loja.Entity.CRM.AssinaturaClube;
import br.com.otica.otica_loja.enums.StatusAssinatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssinaturaClubeRepository extends JpaRepository<AssinaturaClube, UUID> {

    Optional<AssinaturaClube> findByUsuarioId(UUID usuarioId);

    List<AssinaturaClube> findByStatus(StatusAssinatura status);

    // Útil para o Admin ver quem precisa renovar este mês
    List<AssinaturaClube> findByDataRenovacaoBetween(OffsetDateTime inicio, OffsetDateTime fim);

    // Membros que já venceram e precisam ser cobrados via WhatsApp
    List<AssinaturaClube> findByStatusAndDataRenovacaoBefore(StatusAssinatura status, OffsetDateTime dataCorte);
}