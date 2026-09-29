package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.SocialShowcase;
import br.com.otica.otica_loja.Repository.CMS.SocialShowcaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrarVisualizacaoUseCase {

    private final SocialShowcaseRepository repository;

    @Transactional
    public void executar(UUID showcaseId) {
        repository.findById(showcaseId).ifPresent(showcase -> {
            showcase.setVisualizacoes(showcase.getVisualizacoes() + 1);
            repository.save(showcase);
        });
    }
}