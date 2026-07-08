package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Campanha;
import br.com.otica.otica_loja.Repository.CMS.CampanhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AtualizarCampanhaUseCase {

    @Autowired
    private CampanhaRepository campanhaRepository;

    /**
     * Atualiza uma campanha existente.
     */
    public Campanha atualizarCampanha(UUID campanhaId,
                                      String nome,
                                      String slug,
                                      String descricao,
                                      String bannerPath,
                                      OffsetDateTime dataInicio,
                                      OffsetDateTime dataFim,
                                      Boolean ativo) {

        // Buscar campanha existente
        Campanha campanha = campanhaRepository.findById(campanhaId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada."));

        // Atualizar campos apenas se informados
        if (nome != null) campanha.setNome(nome);

        if (slug != null && !slug.equals(campanha.getSlug())) {
            // Validação: não permitir duplicação de slug
            if (campanhaRepository.existsBySlug(slug)) {
                throw new IllegalArgumentException("Já existe uma campanha com este slug.");
            }
            campanha.setSlug(slug);
        }

        if (descricao != null) campanha.setDescricao(descricao);
        if (bannerPath != null) campanha.setBannerPath(bannerPath);
        if (dataInicio != null) campanha.setDataInicio(dataInicio);
        if (dataFim != null) campanha.setDataFim(dataFim);
        if (ativo != null) campanha.setAtivo(ativo);

        // Atualizar timestamp
        campanha.setAtualizadoEm(OffsetDateTime.now());

        return campanhaRepository.save(campanha);
    }
}
