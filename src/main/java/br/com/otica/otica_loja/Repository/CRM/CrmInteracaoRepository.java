package br.com.otica.otica_loja.Repository.CRM;

import br.com.otica.otica_loja.Entity.CRM.CrmInteracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CrmInteracaoRepository extends JpaRepository<CrmInteracao, UUID> {

    // Buscar todas as interações de um usuário
    List<CrmInteracao> findByUsuarioId(UUID usuarioId);

    // Buscar interações por tipo (ex.: "compra", "suporte", "newsletter")
    List<CrmInteracao> findByTipo(String tipo);

    // Buscar interações de um usuário por tipo
    List<CrmInteracao> findByUsuarioIdAndTipo(UUID usuarioId, String tipo);

    // Buscar interações criadas após uma data
    List<CrmInteracao> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar interações criadas antes de uma data
    List<CrmInteracao> findByCriadoEmBefore(OffsetDateTime data);
}
