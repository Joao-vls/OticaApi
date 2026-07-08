package br.com.otica.otica_loja.Repository.CRM;

import br.com.otica.otica_loja.Entity.CRM.CampanhaEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CampanhaEmailRepository extends JpaRepository<CampanhaEmail, UUID> {

    // Buscar campanhas por título
    List<CampanhaEmail> findByTituloContainingIgnoreCase(String titulo);

    // Buscar campanhas por assunto
    List<CampanhaEmail> findByAssuntoContainingIgnoreCase(String assunto);

    // Buscar campanhas agendadas para uma data específica
    List<CampanhaEmail> findByAgendadaPara(OffsetDateTime agendadaPara);

    // Buscar campanhas já enviadas
    List<CampanhaEmail> findByEnviadaTrue();

    // Buscar campanhas ainda não enviadas
    List<CampanhaEmail> findByEnviadaFalse();

    // Buscar campanhas agendadas mas não enviadas
    List<CampanhaEmail> findByAgendadaParaAfterAndEnviadaFalse(OffsetDateTime now);

    // Buscar campanhas criadas recentemente
    List<CampanhaEmail> findByCriadoEmAfter(OffsetDateTime data);
}
