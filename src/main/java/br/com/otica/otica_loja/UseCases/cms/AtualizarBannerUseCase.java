package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Banner;
import br.com.otica.otica_loja.Repository.CMS.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AtualizarBannerUseCase {

    @Autowired
    private BannerRepository bannerRepository;

    /**
     * Atualiza um banner existente.
     */
    public Banner atualizarBanner(UUID bannerId,
                                  String titulo,
                                  String subtitulo,
                                  String descricao,
                                  String imagemDesktopPath,
                                  String imagemMobilePath,
                                  String linkUrl,
                                  String botaoTexto,
                                  String posicao,
                                  Boolean ativo,
                                  OffsetDateTime dataInicio,
                                  OffsetDateTime dataFim) {

        // Buscar banner existente
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Banner não encontrado."));

        // Atualizar campos
        if (titulo != null) banner.setTitulo(titulo);
        if (subtitulo != null) banner.setSubtitulo(subtitulo);
        if (descricao != null) banner.setDescricao(descricao);
        if (imagemDesktopPath != null) banner.setImagemDesktopPath(imagemDesktopPath);
        if (imagemMobilePath != null) banner.setImagemMobilePath(imagemMobilePath);
        if (linkUrl != null) banner.setLinkUrl(linkUrl);
        if (botaoTexto != null) banner.setBotaoTexto(botaoTexto);
        if (posicao != null) banner.setPosicao(posicao);
        if (ativo != null) banner.setAtivo(ativo);
        if (dataInicio != null) banner.setDataInicio(dataInicio);
        if (dataFim != null) banner.setDataFim(dataFim);

        // Atualizar timestamp
        banner.setAtualizadoEm(OffsetDateTime.now());

        return bannerRepository.save(banner);
    }
}
