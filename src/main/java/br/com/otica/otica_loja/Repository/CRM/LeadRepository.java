package br.com.otica.otica_loja.Repository.CRM;

import br.com.otica.otica_loja.Entity.CRM.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {

    // Buscar lead pelo e-mail
    Optional<Lead> findByEmail(String email);

    // Buscar lead pelo telefone
    Optional<Lead> findByTelefone(String telefone);

    // Verificar se já existe lead com determinado e-mail
    boolean existsByEmail(String email);

    // Buscar leads por origem (ex.: campanha, indicação, site)
    List<Lead> findByOrigem(String origem);

    // Buscar leads convertidos
    List<Lead> findByConvertidoTrue();

    // Buscar leads não convertidos
    List<Lead> findByConvertidoFalse();

    // Buscar leads criados após uma data
    List<Lead> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar leads criados antes de uma data
    List<Lead> findByCriadoEmBefore(OffsetDateTime data);
}
