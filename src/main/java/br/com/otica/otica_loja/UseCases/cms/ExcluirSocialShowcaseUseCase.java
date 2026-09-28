package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.SocialShowcase;
import br.com.otica.otica_loja.Repository.CMS.SocialShowcaseRepository;
import br.com.otica.otica_loja.service.CloudinaryStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcluirSocialShowcaseUseCase {

    private final SocialShowcaseRepository repository;
    private final CloudinaryStorageService cloudinaryStorageService;

    @Transactional
    public void executar(UUID id) {
        SocialShowcase showcase = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showcase não encontrado com ID: " + id));

        // Remove os arquivos físicos do Cloudinary
        cloudinaryStorageService.deletarArquivoPorUrl(showcase.getVideoPath());
        cloudinaryStorageService.deletarArquivoPorUrl(showcase.getThumbnailPath());

        repository.delete(showcase);
    }
}