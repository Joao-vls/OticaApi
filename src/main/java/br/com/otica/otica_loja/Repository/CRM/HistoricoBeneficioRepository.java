package br.com.otica.otica_loja.Repository.CRM;

import br.com.otica.otica_loja.Entity.CRM.HistoricoBeneficio;
import br.com.otica.otica_loja.enums.TipoBeneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistoricoBeneficioRepository extends JpaRepository<HistoricoBeneficio, UUID> {

    List<HistoricoBeneficio> findByAssinaturaIdOrderByDataUtilizacaoDesc(UUID assinaturaId);

    // Útil para verificar se o cliente já usou a troca anual neste ciclo
    List<HistoricoBeneficio> findByAssinaturaIdAndTipo(UUID assinaturaId, TipoBeneficio tipo);
}