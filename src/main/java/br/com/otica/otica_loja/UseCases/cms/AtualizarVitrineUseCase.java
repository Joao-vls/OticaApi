package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Repository.CMS.VitrineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AtualizarVitrineUseCase {

    @Autowired
    private VitrineRepository vitrineRepository;

    /**
     * Atualiza uma vitrine existente.
     */
    public Vitrine atualizarVitrine(UUID vitrineId,
                                    String nome,
                                    String slug,
                                    String titulo,
                                    String subtitulo,
                                    Integer ordem,
                                    Boolean ativo) {

        // Buscar vitrine existente
        Vitrine vitrine = vitrineRepository.findById(vitrineId)
                .orElseThrow(() -> new IllegalArgumentException("Vitrine não encontrada."));

        // Atualizar campos apenas se informados
        if (nome != null) vitrine.setNome(nome);
        if (slug != null && !slug.equals(vitrine.getSlug())) {
            // Validação: não permitir duplicação de slug
            if (vitrineRepository.existsBySlug(slug)) {
                throw new IllegalArgumentException("Já existe uma vitrine com este slug.");
            }
            vitrine.setSlug(slug);
        }
        if (titulo != null) vitrine.setTitulo(titulo);
        if (subtitulo != null) vitrine.setSubtitulo(subtitulo);
        if (ordem != null) vitrine.setOrdem(ordem);
        if (ativo != null) vitrine.setAtivo(ativo);

        // Atualizar timestamp
        vitrine.setAtualizadoEm(OffsetDateTime.now());

        return vitrineRepository.save(vitrine);
    }
}
