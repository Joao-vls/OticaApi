package br.com.otica.otica_loja.Repository.Admin;

import br.com.otica.otica_loja.Entity.Admin.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, UUID> {

    // Buscar registros de auditoria por entidade
    List<Auditoria> findByEntidade(String entidade);

    // Buscar registros de auditoria por entidade e ID específico
    List<Auditoria> findByEntidadeAndEntidadeId(String entidade, UUID entidadeId);

    // Buscar registros de auditoria por usuário
    List<Auditoria> findByUsuarioId(UUID usuarioId);

    // Buscar registros de auditoria por ação (CREATE, UPDATE, DELETE, etc.)
    List<Auditoria> findByAcao(String acao);
}
