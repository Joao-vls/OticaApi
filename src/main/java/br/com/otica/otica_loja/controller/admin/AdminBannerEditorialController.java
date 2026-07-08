package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.CMS.BannerEditorial;
import br.com.otica.otica_loja.Repository.CMS.BannerEditorialRepository;
import br.com.otica.otica_loja.UseCases.cms.SalvarBannerEditorialUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/banners-editoriais")
@RequiredArgsConstructor
public class AdminBannerEditorialController {

    private final SalvarBannerEditorialUseCase salvarBannerEditorialUseCase;
    private final BannerEditorialRepository bannerEditorialRepository;

    @GetMapping
    public ResponseEntity<List<BannerEditorial>> listarTodos() {
        List<BannerEditorial> lista = bannerEditorialRepository.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{identificador}")
    public ResponseEntity<BannerEditorial> buscarPorIdentificador(@PathVariable String identificador) {
        return bannerEditorialRepository.findByIdentificador(identificador)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BannerEditorial> salvarOuAtualizar(
            @RequestParam String identificador,
            @RequestParam String layoutTipo,
            @RequestParam(required = false) String textoMarca,
            @RequestPart(required = false) MultipartFile logoFile,
            @RequestParam(required = false) String logoUrl, // Novo: URL de logo opcional

            // Parâmetros da Seção 1 (Topo / Esquerda)
            @RequestPart(required = false) MultipartFile sec1MediaFile,
            @RequestParam(required = false) String sec1MediaUrl, // Novo: URL de mídia opcional para Seção 1
            @RequestParam(required = false) String sec1Titulo,
            @RequestParam(required = false) String sec1TituloDestaque,
            @RequestParam(required = false) String sec1Descricao,
            @RequestParam(required = false) String sec1ProdutoNome,
            @RequestParam(required = false) BigDecimal sec1Preco,
            @RequestParam(required = false) Integer sec1Desconto,
            @RequestParam(required = false) String sec1LinkUrl,

            // Parâmetros da Seção 2 (Fundo / Direita)
            @RequestPart(required = false) MultipartFile sec2MediaFile,
            @RequestParam(required = false) String sec2MediaUrl, // Novo: URL de mídia opcional para Seção 2
            @RequestParam(required = false) String sec2Titulo,
            @RequestParam(required = false) String sec2TituloDestaque,
            @RequestParam(required = false) String sec2Descricao,
            @RequestParam(required = false) String sec2ProdutoNome,
            @RequestParam(required = false) BigDecimal sec2Preco,
            @RequestParam(required = false) Integer sec2Desconto,
            @RequestParam(required = false) String sec2LinkUrl
    ) throws IOException {

        // Limpeza preventiva: se o arquivo foi enviado vazio pelo cliente REST, força como nulo
        MultipartFile logoFinal = (logoFile != null && !logoFile.isEmpty()) ? logoFile : null;
        MultipartFile sec1MediaFinal = (sec1MediaFile != null && !sec1MediaFile.isEmpty()) ? sec1MediaFile : null;
        MultipartFile sec2MediaFinal = (sec2MediaFile != null && !sec2MediaFile.isEmpty()) ? sec2MediaFile : null;

        // Executa o Use Case passando tanto os arquivos quanto as strings de URL
        BannerEditorial banner = salvarBannerEditorialUseCase.salvarOuAtualizar(
                identificador,
                layoutTipo,
                textoMarca,
                logoFinal,
                logoUrl,
                sec1MediaFinal,
                sec1MediaUrl,
                sec1Titulo,
                sec1TituloDestaque,
                sec1Descricao,
                sec1ProdutoNome,
                sec1Preco,
                sec1Desconto,
                sec1LinkUrl,
                sec2MediaFinal,
                sec2MediaUrl,
                sec2Titulo,
                sec2TituloDestaque,
                sec2Descricao,
                sec2ProdutoNome,
                sec2Preco,
                sec2Desconto,
                sec2LinkUrl
        );

        return ResponseEntity.ok(banner);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarBannerEditorial(@PathVariable UUID id) {
        if (!bannerEditorialRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bannerEditorialRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}