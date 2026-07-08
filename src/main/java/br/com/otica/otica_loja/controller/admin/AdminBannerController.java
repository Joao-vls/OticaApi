package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.CMS.Banner;
import br.com.otica.otica_loja.UseCases.cms.CriarBannerUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final CriarBannerUseCase criarBannerUseCase;

    @GetMapping
    public ResponseEntity<String> listarBanners() {
        return ResponseEntity.ok("lista de banners");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Banner> criarBanner(

            @RequestParam String titulo,

            @RequestParam(required = false)
            String subtitulo,

            @RequestParam(required = false)
            String descricao,

            @RequestPart(required = false)
            MultipartFile imagemDesktop,

            @RequestPart(required = false)
            MultipartFile imagemMobile,

            @RequestParam(required = false)
            String linkUrl,

            @RequestParam(required = false)
            String botaoTexto,

            @RequestParam
            String posicao,

            @RequestParam
            OffsetDateTime dataInicio,

            @RequestParam
            OffsetDateTime dataFim

    ) throws IOException {

        Banner banner = criarBannerUseCase.criarBanner(
                titulo,
                subtitulo,
                descricao,
                imagemDesktop,
                imagemMobile,
                linkUrl,
                botaoTexto,
                posicao,
                dataInicio,
                dataFim
        );

        return ResponseEntity.ok(banner);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarBanner(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                "banner atualizado (id=" + id + ")"
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarBanner(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                "banner deletado (id=" + id + ")"
        );
    }
}