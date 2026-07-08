package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Campanha;
import br.com.otica.otica_loja.Repository.CMS.CampanhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExcluirCampanhaUseCase {

    @Autowired
    private CampanhaRepository campanhaRepository;

    /**
     * Exclusão lógica: desativa a campanha mantendo histórico.
     */
    public Campanha excluirCampanha(UUID campanhaId) {
        Campanha campanha = campanhaRepository.findById(campanhaId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada."));

        campanha.setAtivo(false);
        campanha.setAtualizadoEm(java.time.OffsetDateTime.now());

        return campanhaRepository.save(campanha);
    }

    /**
     * Exclusão física: remove a campanha do banco de dados.
     */
    public void excluirCampanhaFisicamente(UUID campanhaId) {
        if (!campanhaRepository.existsById(campanhaId)) {
            throw new IllegalArgumentException("Campanha não encontrada.");
        }
        campanhaRepository.deleteById(campanhaId);
    }
}
