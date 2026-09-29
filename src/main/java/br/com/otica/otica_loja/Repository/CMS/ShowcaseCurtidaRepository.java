package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.ShowcaseCurtida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShowcaseCurtidaRepository extends JpaRepository<ShowcaseCurtida, UUID> {

    // Verifica se o usuário já curtiu este vídeo
    boolean existsByUsuarioIdAndShowcaseId(UUID usuarioId, UUID showcaseId);

    // Caso queira implementar a funcionalidade de "descurtir" no futuro
    void deleteByUsuarioIdAndShowcaseId(UUID usuarioId, UUID showcaseId);
}