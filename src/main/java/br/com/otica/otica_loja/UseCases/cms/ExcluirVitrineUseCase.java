package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Repository.CMS.VitrineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExcluirVitrineUseCase {

    private final VitrineRepository vitrineRepository;

    public ExcluirVitrineUseCase(VitrineRepository vitrineRepository) {
        this.vitrineRepository = vitrineRepository;
    }

    @Transactional
    public void executar(UUID id) {
        if (!vitrineRepository.existsById(id)) {
            throw new IllegalArgumentException("Vitrine não encontrada com o ID: " + id);
        }
        vitrineRepository.deleteById(id);
    }
}