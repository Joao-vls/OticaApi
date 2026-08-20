package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoMidiaRepository;
import br.com.otica.otica_loja.dto.ProdutoMidiaUpdateDTO;
import br.com.otica.otica_loja.enums.TipoMidia;
import br.com.otica.otica_loja.service.cms.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AtualizarMidiaUseCase {

    private final ProdutoMidiaRepository midiaRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public void processarMidias(
            Produto produto,
            List<ProdutoMidiaUpdateDTO> midiasDto,
            Map<String, ProdutoVariante> variantesMapa,
            MultipartHttpServletRequest request
    ) throws IOException {

        if (midiasDto == null || midiasDto.isEmpty()) {
            return;
        }

        for (ProdutoMidiaUpdateDTO midiaDto : midiasDto) {
            // 1. Remoção
            if (Boolean.TRUE.equals(midiaDto.getRemover()) && midiaDto.getId() != null) {
                midiaRepository.deleteById(midiaDto.getId());
                continue;
            }

            // 2. Busca pelo ID ou cria nova entidade
            ProdutoMidia midia;
            boolean ehNovaMidia = false;

            if (midiaDto.getId() != null) {
                midia = midiaRepository.findById(midiaDto.getId()).orElse(null);
                if (midia == null) {
                    midia = criarNovaMidia(produto);
                    ehNovaMidia = true;
                }
            } else {
                midia = criarNovaMidia(produto);
                ehNovaMidia = true;
            }

            // 3. Configuração de campos básicos
            TipoMidia tipoMidia = midiaDto.getTipo() != null ? midiaDto.getTipo() : TipoMidia.IMAGE;
            midia.setOrdem(midiaDto.getOrdem() != null ? midiaDto.getOrdem() : 0);
            midia.setTipo(tipoMidia);

            // 4. Identificador único para a chave do MultipartFile
            String chaveIdentificadora = midiaDto.getId() != null ? midiaDto.getId().toString() : midiaDto.getUniqueId();

            MultipartFile arquivoFisico = null;
            MultipartFile thumbnailFisica = null;

            if (chaveIdentificadora != null && !chaveIdentificadora.isBlank()) {
                arquivoFisico = request.getFile("file_" + chaveIdentificadora);
                thumbnailFisica = request.getFile("thumb_" + chaveIdentificadora);
            }

            // 5. Preservação de URLs antigas
            String pathAnterior = (midiaDto.getPath() != null && !midiaDto.getPath().isBlank())
                    ? midiaDto.getPath()
                    : midia.getPath();

            String thumbAnteriorFromDto = midiaDto.getThumbnailPath();
            if (thumbAnteriorFromDto != null && !thumbAnteriorFromDto.startsWith("http://") && !thumbAnteriorFromDto.startsWith("https://")) {
                thumbAnteriorFromDto = null;
            }

            String thumbAnterior = (thumbAnteriorFromDto != null && !thumbAnteriorFromDto.isBlank())
                    ? thumbAnteriorFromDto
                    : midia.getThumbnailPath();

            // 6. Resolução dos caminhos
            String pathResolvido = resolverCaminhoMidia(arquivoFisico, midiaDto.getUrlExterna(), pathAnterior, tipoMidia);
            String thumbResolvido = resolverCaminhoMidia(thumbnailFisica, null, thumbAnterior, TipoMidia.IMAGE);

            if (pathResolvido == null || pathResolvido.isBlank()) {
                if (ehNovaMidia) {
                    throw new IllegalArgumentException(
                            "O arquivo ou URL da mídia de ordem " + midia.getOrdem() + " do tipo " + midia.getTipo() + " não foi fornecido."
                    );
                }
            } else {
                midia.setPath(pathResolvido);
            }

            if (tipoMidia == TipoMidia.VIDEO && thumbResolvido != null && !thumbResolvido.isBlank()) {
                midia.setThumbnailPath(thumbResolvido);
            } else {
                midia.setThumbnailPath(null);
            }

            // 7. Associação com a variante
            if (midiaDto.getRefVariante() != null && variantesMapa != null && variantesMapa.containsKey(midiaDto.getRefVariante())) {
                midia.setVariante(variantesMapa.get(midiaDto.getRefVariante()));
            } else {
                midia.setVariante(null);
            }

            midiaRepository.save(midia);
        }
    }

    private ProdutoMidia criarNovaMidia(Produto produto) {
        ProdutoMidia midia = new ProdutoMidia();
        midia.setProduto(produto);
        midia.setCriadoEm(OffsetDateTime.now());
        return midia;
    }

    private String resolverCaminhoMidia(
            MultipartFile arquivo,
            String urlExterna,
            String pathAnterior,
            TipoMidia tipo
    ) throws IOException {

        if (arquivo != null && !arquivo.isEmpty()) {
            return cloudinaryService.upload(arquivo, tipo);
        }

        if (urlExterna != null && !urlExterna.isBlank()) {
            return urlExterna;
        }

        return pathAnterior;
    }
}