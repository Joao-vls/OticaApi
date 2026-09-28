package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.SocialShowcase;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.CMS.SocialShowcaseRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.dto.SocialShowcaseRequestDTO;
import br.com.otica.otica_loja.enums.TipoMidia;
import br.com.otica.otica_loja.service.cms.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CriarSocialShowcaseUseCase {

    private final SocialShowcaseRepository repository;
    private final ProdutoRepository produtoRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public SocialShowcase executar(
            SocialShowcaseRequestDTO dto,
            MultipartFile videoFile,
            MultipartFile thumbnailFile
    ) throws IOException {

        SocialShowcase showcase = new SocialShowcase();
        showcase.setMarcaNome(dto.marcaNome());
        showcase.setModeloNome(dto.modeloNome());
        showcase.setUsername(dto.username());
        showcase.setOrdem(dto.ordem() != null ? dto.ordem() : 0);
        showcase.setAtivo(dto.ativo() != null ? dto.ativo() : true);
        showcase.setCriadoEm(OffsetDateTime.now());

        if (dto.produtoId() != null) {
            Produto produto = produtoRepository.findById(dto.produtoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
            showcase.setProduto(produto);
        }

        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("O arquivo de vídeo é obrigatório.");
        }

        if (thumbnailFile == null || thumbnailFile.isEmpty()) {
            throw new IllegalArgumentException("A thumbnail do vídeo é obrigatória.");
        }

        String videoUrl = cloudinaryService.upload(videoFile, TipoMidia.VIDEO);
        String thumbnailUrl = cloudinaryService.upload(thumbnailFile, TipoMidia.IMAGE);

        showcase.setVideoPath(videoUrl);
        showcase.setThumbnailPath(thumbnailUrl);

        return repository.save(showcase);
    }
}