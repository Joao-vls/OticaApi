package br.com.otica.otica_loja.UseCases.midias;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoMidiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrdenarMidiasProdutoUseCase {

    @Autowired
    private ProdutoMidiaRepository midiaRepository;

    /**
     * Atualiza a ordem das mídias de um produto ou variante.
     * Recebe uma lista de IDs na ordem desejada.
     */
    public void ordenarMidias(List<UUID> midiasOrdenadas) {
        int ordem = 1;
        for (UUID midiaId : midiasOrdenadas) {
            ProdutoMidia midia = midiaRepository.findById(midiaId)
                    .orElseThrow(() -> new IllegalArgumentException("Mídia não encontrada: " + midiaId));

            midia.setOrdem(ordem++);
            midiaRepository.save(midia);
        }
    }
}