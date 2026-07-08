package br.com.otica.otica_loja.Repository.Logistica;

import br.com.otica.otica_loja.Entity.Logistica.EnvioEvento;
import br.com.otica.otica_loja.Entity.Logistica.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EnvioEventoRepository extends JpaRepository<EnvioEvento, UUID> {

    // Buscar eventos de um envio específico
    List<EnvioEvento> findByEnvio(Envio envio);

    // Buscar eventos de um envio ordenados por data de criação
    List<EnvioEvento> findByEnvioOrderByCriadoEmAsc(Envio envio);

    // Buscar eventos criados após uma data
    List<EnvioEvento> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar eventos criados antes de uma data
    List<EnvioEvento> findByCriadoEmBefore(OffsetDateTime data);

    // Buscar eventos por descrição (texto parcial)
    List<EnvioEvento> findByDescricaoContainingIgnoreCase(String descricao);

    // Buscar eventos por localização
    List<EnvioEvento> findByLocalizacaoContainingIgnoreCase(String localizacao);
}
