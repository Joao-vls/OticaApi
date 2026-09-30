package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.ShowcaseCurtida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShowcaseCurtidaRepository extends JpaRepository<ShowcaseCurtida, UUID> {

    // Verifica se o usuário já curtiu este vídeo
    boolean existsByUsuarioIdAndShowcaseId(UUID usuarioId, UUID showcaseId);

    // Caso queira implementar a funcionalidade de "descurtir" no futuro
    void deleteByUsuarioIdAndShowcaseId(UUID usuarioId, UUID showcaseId);

    @Query("SELECT c.showcaseId FROM ShowcaseCurtida c WHERE c.usuarioId = :usuarioId")
    List<UUID> findShowcaseIdsByUsuarioId(@Param("usuarioId") UUID usuarioId);
}