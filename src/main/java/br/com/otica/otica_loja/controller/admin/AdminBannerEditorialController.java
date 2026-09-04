package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.CMS.BannerEditorial;
import br.com.otica.otica_loja.Repository.CMS.BannerEditorialRepository;
import br.com.otica.otica_loja.UseCases.cms.AtualizarBannerEditorialUseCase;
import br.com.otica.otica_loja.UseCases.cms.CriarBannerEditorialUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/banners-editoriais")
@RequiredArgsConstructor
public class AdminBannerEditorialController {

    private final CriarBannerEditorialUseCase criarBannerEditorialUseCase;
    private final AtualizarBannerEditorialUseCase atualizarBannerEditorialUseCase;
    private final BannerEditorialRepository bannerEditorialRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<BannerEditorial>> listarTodos() {
        return ResponseEntity.ok(bannerEditorialRepository.findAll());
    }

    @GetMapping("/{identificador}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<BannerEditorial> buscarPorIdentificador(@PathVariable String identificador) {
        return bannerEditorialRepository.findByIdentificador(identificador)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BannerEditorial> criar(
            @RequestParam String identificador,
            @RequestParam String layoutTipo,
            @RequestParam(required = false, defaultValue = "TIPO_1") String estiloVisual,
            @RequestParam(required = false, defaultValue = "true") Boolean ativo,
            @RequestParam(required = false) String textoMarca,
            @RequestPart(required = false) MultipartFile logoFile,
            @RequestParam(required = false) String logoUrl,

            @RequestPart(required = false) MultipartFile sec1MediaFile,
            @RequestParam(required = false) String sec1MediaUrl,
            @RequestParam(required = false) String sec1Titulo,
            @RequestParam(required = false) String sec1TituloDestaque,
            @RequestParam(required = false) String sec1Descricao,
            @RequestParam(required = false) String sec1ProdutoNome,
            @RequestParam(required = false) BigDecimal sec1Preco,
            @RequestParam(required = false) Integer sec1Desconto,
            @RequestParam(required = false) String sec1LinkUrl,

            @RequestPart(required = false) MultipartFile sec2MediaFile,
            @RequestParam(required = false) String sec2MediaUrl,
            @RequestParam(required = false) String sec2Titulo,
            @RequestParam(required = false) String sec2TituloDestaque,
            @RequestParam(required = false) String sec2Descricao,
            @RequestParam(required = false) String sec2ProdutoNome,
            @RequestParam(required = false) BigDecimal sec2Preco,
            @RequestParam(required = false) Integer sec2Desconto,
            @RequestParam(required = false) String sec2LinkUrl
    ) throws IOException {

        String layoutSanitizado = validarELimparLayoutTipo(layoutTipo);

        BannerEditorial banner = criarBannerEditorialUseCase.executar(
                identificador, layoutSanitizado, estiloVisual, textoMarca,
                limparArquivoVazio(logoFile), logoUrl,
                limparArquivoVazio(sec1MediaFile), sec1MediaUrl, sec1Titulo, sec1TituloDestaque,
                sec1Descricao, sec1ProdutoNome, sec1Preco, sec1Desconto, sec1LinkUrl,
                limparArquivoVazio(sec2MediaFile), sec2MediaUrl, sec2Titulo, sec2TituloDestaque,
                sec2Descricao, sec2ProdutoNome, sec2Preco, sec2Desconto, sec2LinkUrl
        );

        banner.setAtivo(ativo != null ? ativo : true);
        banner.setAtualizadoEm(OffsetDateTime.now());
        banner = bannerEditorialRepository.save(banner);

        return ResponseEntity.status(HttpStatus.CREATED).body(banner);
    }

    @PutMapping(value = "/{identificador}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BannerEditorial> atualizar(
            @PathVariable String identificador,
            @RequestParam String layoutTipo,
            @RequestParam(required = false, defaultValue = "TIPO_1") String estiloVisual,
            @RequestParam(required = false, defaultValue = "true") Boolean ativo,
            @RequestParam(required = false) String textoMarca,
            @RequestPart(required = false) MultipartFile logoFile,
            @RequestParam(required = false) String logoUrl,

            @RequestPart(required = false) MultipartFile sec1MediaFile,
            @RequestParam(required = false) String sec1MediaUrl,
            @RequestParam(required = false) String sec1Titulo,
            @RequestParam(required = false) String sec1TituloDestaque,
            @RequestParam(required = false) String sec1Descricao,
            @RequestParam(required = false) String sec1ProdutoNome,
            @RequestParam(required = false) BigDecimal sec1Preco,
            @RequestParam(required = false) Integer sec1Desconto,
            @RequestParam(required = false) String sec1LinkUrl,

            @RequestPart(required = false) MultipartFile sec2MediaFile,
            @RequestParam(required = false) String sec2MediaUrl,
            @RequestParam(required = false) String sec2Titulo,
            @RequestParam(required = false) String sec2TituloDestaque,
            @RequestParam(required = false) String sec2Descricao,
            @RequestParam(required = false) String sec2ProdutoNome,
            @RequestParam(required = false) BigDecimal sec2Preco,
            @RequestParam(required = false) Integer sec2Desconto,
            @RequestParam(required = false) String sec2LinkUrl
    ) throws IOException {

        String layoutSanitizado = validarELimparLayoutTipo(layoutTipo);

        BannerEditorial banner = atualizarBannerEditorialUseCase.executar(
                identificador, layoutSanitizado, estiloVisual, textoMarca,
                limparArquivoVazio(logoFile), logoUrl,
                limparArquivoVazio(sec1MediaFile), sec1MediaUrl, sec1Titulo, sec1TituloDestaque,
                sec1Descricao, sec1ProdutoNome, sec1Preco, sec1Desconto, sec1LinkUrl,
                limparArquivoVazio(sec2MediaFile), sec2MediaUrl, sec2Titulo, sec2TituloDestaque,
                sec2Descricao, sec2ProdutoNome, sec2Preco, sec2Desconto, sec2LinkUrl
        );

        banner.setAtivo(ativo != null ? ativo : true);
        banner.setAtualizadoEm(OffsetDateTime.now());
        banner = bannerEditorialRepository.save(banner);

        return ResponseEntity.ok(banner);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BannerEditorial> alternarStatus(
            @PathVariable UUID id,
            @RequestParam Boolean ativo
    ) {
        return bannerEditorialRepository.findById(id)
                .map(banner -> {
                    banner.setAtivo(ativo);
                    banner.setAtualizadoEm(OffsetDateTime.now());
                    return ResponseEntity.ok(bannerEditorialRepository.save(banner));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarBannerEditorial(@PathVariable UUID id) {
        if (!bannerEditorialRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bannerEditorialRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private String validarELimparLayoutTipo(String layoutTipo) {
        if (layoutTipo == null) {
            return "HORIZONTAL";
        }
        String valorNorm = layoutTipo.trim().toUpperCase();
        if ("HORIZONTAL".equals(valorNorm) || "VERTICAL".equals(valorNorm)) {
            return valorNorm;
        }
        throw new IllegalArgumentException("Tipo de layout inválido. Aceitos apenas: HORIZONTAL ou VERTICAL.");
    }

    private MultipartFile limparArquivoVazio(MultipartFile file) {
        return (file != null && !file.isEmpty()) ? file : null;
    }
}