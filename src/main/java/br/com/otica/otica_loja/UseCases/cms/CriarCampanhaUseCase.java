package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Campanha;
import br.com.otica.otica_loja.Repository.CMS.CampanhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class CriarCampanhaUseCase {

    @Autowired
    private CampanhaRepository campanhaRepository;

    /**
     * Cria uma nova campanha no CMS.
     */
    public Campanha criarCampanha(String nome,
                                  String slug,
                                  String descricao,
                                  String bannerPath,
                                  OffsetDateTime dataInicio,
                                  OffsetDateTime dataFim,
                                  Boolean ativo) {

        // Validação: não permitir campanhas duplicadas pelo slug
        if (campanhaRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Já existe uma campanha com este slug.");
        }

        Campanha campanha = new Campanha();
        campanha.setNome(nome);
        campanha.setSlug(slug);
        campanha.setDescricao(descricao);
        campanha.setBannerPath(bannerPath);
        campanha.setDataInicio(dataInicio);
        campanha.setDataFim(dataFim);
        campanha.setAtivo(ativo != null ? ativo : true);
        campanha.setCriadoEm(OffsetDateTime.now());
        campanha.setAtualizadoEm(OffsetDateTime.now());

        return campanhaRepository.save(campanha);
    }
}
