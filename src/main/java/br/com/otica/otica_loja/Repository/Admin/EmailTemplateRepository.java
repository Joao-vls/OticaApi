package br.com.otica.otica_loja.Repository.Admin;

import br.com.otica.otica_loja.Entity.Admin.EmailTemplate;
import br.com.otica.otica_loja.enums.TipoEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, UUID> {
    Optional<EmailTemplate> findByTipo(TipoEmail tipo);
}