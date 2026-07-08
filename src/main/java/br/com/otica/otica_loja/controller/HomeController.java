package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.Repository.CMS.BannerEditorialRepository;
import br.com.otica.otica_loja.UseCases.cms.BuscarVitrinePorSlugUseCase;
import br.com.otica.otica_loja.dto.cms.PromoMainResponseDTO;
import br.com.otica.otica_loja.dto.cms.PromoSectionDTO;
import br.com.otica.otica_loja.dto.cms.VitrineResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeController {

    private final BannerEditorialRepository bannerEditorialRepository;

    // Agora aceita tanto /home/promo/HOME_PROMO_MAIN quanto /home/promo/HOME_PROMO_SIDE
    @GetMapping("/promo/{identificador}")
    public ResponseEntity<PromoMainResponseDTO> getPromoPorIdentificador(@PathVariable String identificador) {

        return bannerEditorialRepository.findByIdentificador(identificador)
                .map(banner -> {

                    PromoSectionDTO topSection = new PromoSectionDTO(
                            banner.getSec1MediaPath(),
                            banner.getSec1Titulo(),
                            banner.getSec1TituloDestaque(),
                            banner.getSec1Titulo(),
                            banner.getSec1TituloDestaque(),
                            banner.getSec1ProdutoNome(),
                            banner.getSec1Descricao(),
                            banner.getSec1Preco(),
                            banner.getSec1Desconto(),
                            banner.getSec1LinkUrl()
                    );

                    PromoSectionDTO bottomSection = new PromoSectionDTO(
                            banner.getSec2MediaPath(),
                            banner.getSec2Titulo(),
                            banner.getSec2TituloDestaque(),
                            banner.getSec2Titulo(),
                            banner.getSec2TituloDestaque(),
                            banner.getSec2ProdutoNome(),
                            banner.getSec2Descricao(),
                            banner.getSec2Preco(),
                            banner.getSec2Desconto(),
                            banner.getSec2LinkUrl()
                    );

                    PromoMainResponseDTO response = new PromoMainResponseDTO(
                            banner.getTextoMarca() != null ? banner.getTextoMarca() : "LERNERRA",
                            banner.getLogoPath() != null ? banner.getLogoPath() : "assets/logo.png",
                            topSection,
                            bottomSection
                    );

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    // Injete o Use Case no construtor da HomeController junto com o bannerEditorialRepository
    private final BuscarVitrinePorSlugUseCase buscarVitrinePorSlugUseCase;

    @GetMapping("/vitrine/{slug}")
    public ResponseEntity<VitrineResponseDTO> getVitrinePorSlug(@PathVariable String slug) {
        try {
            VitrineResponseDTO vitrine = buscarVitrinePorSlugUseCase.executar(slug);
            return ResponseEntity.ok(vitrine);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}