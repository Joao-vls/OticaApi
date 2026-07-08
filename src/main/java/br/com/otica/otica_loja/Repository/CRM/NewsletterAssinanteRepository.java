package br.com.otica.otica_loja.Repository.CRM;

import br.com.otica.otica_loja.Entity.CRM.NewsletterAssinante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsletterAssinanteRepository extends JpaRepository<NewsletterAssinante, UUID> {

    // Buscar assinante pelo e-mail
    Optional<NewsletterAssinante> findByEmail(String email);

    // Verificar se já existe assinante com determinado e-mail
    boolean existsByEmail(String email);

    // Buscar assinantes ativos
    List<NewsletterAssinante> findByAtivoTrue();

    // Buscar assinantes inativos
    List<NewsletterAssinante> findByAtivoFalse();

    // Buscar assinantes por origem (ex.: campanha, site, indicação)
    List<NewsletterAssinante> findByOrigem(String origem);

    // Buscar assinantes criados após uma data
    List<NewsletterAssinante> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar assinantes criados antes de uma data
    List<NewsletterAssinante> findByCriadoEmBefore(OffsetDateTime data);
}
