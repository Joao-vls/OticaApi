package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Repository.CMS.VitrineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarVitrinesUseCase {

    private final VitrineRepository vitrineRepository;

    public ListarVitrinesUseCase(VitrineRepository vitrineRepository) {
        this.vitrineRepository = vitrineRepository;
    }

    @Transactional(readOnly = true)
    public List<Vitrine> executar() {
        return vitrineRepository.findAllByOrderByOrdemAsc();
    }
}