package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Banner;
import br.com.otica.otica_loja.Repository.CMS.BannerRepository;

import br.com.otica.otica_loja.service.cms.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CriarBannerUseCase {

    private final BannerRepository bannerRepository;
    private final CloudinaryService cloudinaryService;

    public Banner criarBanner(
            String titulo,
            String subtitulo,
            String descricao,
            MultipartFile imagemDesktop,
            MultipartFile imagemMobile,
            String linkUrl,
            String botaoTexto,
            String posicao,
            OffsetDateTime dataInicio,
            OffsetDateTime dataFim
    ) throws IOException {

        bannerRepository.findByTitulo(titulo).ifPresent(b -> {
            throw new IllegalArgumentException(
                    "Já existe um banner com este título."
            );
        });

        String imagemDesktopUrl = null;
        String imagemMobileUrl = null;

        if (imagemDesktop != null && !imagemDesktop.isEmpty()) {
            imagemDesktopUrl =
                    cloudinaryService.upload(imagemDesktop);
        }

        if (imagemMobile != null && !imagemMobile.isEmpty()) {
            imagemMobileUrl =
                    cloudinaryService.upload(imagemMobile);
        }

        Banner banner = new Banner();

        banner.setTitulo(titulo);
        banner.setSubtitulo(subtitulo);
        banner.setDescricao(descricao);

        banner.setImagemDesktopPath(imagemDesktopUrl);
        banner.setImagemMobilePath(imagemMobileUrl);

        banner.setLinkUrl(linkUrl);
        banner.setBotaoTexto(botaoTexto);
        banner.setPosicao(posicao);

        banner.setAtivo(true);
        banner.setDataInicio(dataInicio);
        banner.setDataFim(dataFim);

        banner.setCriadoEm(OffsetDateTime.now());
        banner.setAtualizadoEm(OffsetDateTime.now());

        return bannerRepository.save(banner);
    }
}