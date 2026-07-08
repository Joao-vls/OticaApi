package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.BannerEditorial;
import br.com.otica.otica_loja.Repository.CMS.BannerEditorialRepository;
import br.com.otica.otica_loja.service.cms.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class SalvarBannerEditorialUseCase {

    private final BannerEditorialRepository bannerEditorialRepository;
    private final CloudinaryService cloudinaryService;

    public BannerEditorial salvarOuAtualizar(
            String identificador,
            String layoutTipo,
            String textoMarca,

            MultipartFile logoFile,
            String logoUrl,

            MultipartFile sec1MediaFile,
            String sec1MediaUrl,
            String sec1Titulo,
            String sec1TituloDestaque,
            String sec1Descricao,
            String sec1ProdutoNome,
            BigDecimal sec1Preco,
            Integer sec1Desconto,
            String sec1LinkUrl,

            MultipartFile sec2MediaFile,
            String sec2MediaUrl,
            String sec2Titulo,
            String sec2TituloDestaque,
            String sec2Descricao,
            String sec2ProdutoNome,
            BigDecimal sec2Preco,
            Integer sec2Desconto,
            String sec2LinkUrl
    ) throws IOException {

        BannerEditorial banner = bannerEditorialRepository
                .findByIdentificador(identificador)
                .orElseGet(() -> {
                    BannerEditorial novo = new BannerEditorial();
                    novo.setIdentificador(identificador);
                    return novo;
                });

        banner.setLayoutTipo(layoutTipo);
        banner.setTextoMarca(textoMarca);
        banner.setAtivo(true);
        banner.setAtualizadoEm(OffsetDateTime.now());

        // LOGO
        banner.setLogoPath(resolverMidia(logoFile, logoUrl, banner.getLogoPath()));

        // SEÇÃO 1
        banner.setSec1MediaPath(resolverMidia(sec1MediaFile, sec1MediaUrl, banner.getSec1MediaPath()));
        banner.setSec1Titulo(sec1Titulo);
        banner.setSec1TituloDestaque(sec1TituloDestaque);
        banner.setSec1Descricao(sec1Descricao);
        banner.setSec1ProdutoNome(sec1ProdutoNome);
        banner.setSec1LinkUrl(sec1LinkUrl);

        if (sec1Preco != null) {
            banner.setSec1Preco(sec1Preco);
        }

        if (sec1Desconto != null) {
            banner.setSec1Desconto(sec1Desconto);
        }

        // SEÇÃO 2
        banner.setSec2MediaPath(resolverMidia(sec2MediaFile, sec2MediaUrl, banner.getSec2MediaPath()));
        banner.setSec2Titulo(sec2Titulo);
        banner.setSec2TituloDestaque(sec2TituloDestaque);
        banner.setSec2Descricao(sec2Descricao);
        banner.setSec2ProdutoNome(sec2ProdutoNome);
        banner.setSec2LinkUrl(sec2LinkUrl);

        if (sec2Preco != null) {
            banner.setSec2Preco(sec2Preco);
        }

        if (sec2Desconto != null) {
            banner.setSec2Desconto(sec2Desconto);
        }

        return bannerEditorialRepository.save(banner);
    }

    private String resolverMidia(
            MultipartFile file,
            String url,
            String valorAtual
    ) throws IOException {

        if (file != null && !file.isEmpty()) {
            return cloudinaryService.upload(file);
        }

        if (url != null && !url.isBlank()) {
            return url;
        }

        return valorAtual;
    }
}