package br.com.otica.otica_loja.UseCases.faq;

import br.com.otica.otica_loja.Entity.FAQ.FaqCategoria;
import br.com.otica.otica_loja.Entity.FAQ.FaqItem;
import br.com.otica.otica_loja.Repository.FAQ.FaqCategoriaRepository;
import br.com.otica.otica_loja.Repository.FAQ.FaqItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExcluirFaqUseCase {

    @Autowired
    private FaqCategoriaRepository categoriaRepository;

    @Autowired
    private FaqItemRepository itemRepository;

    /**
     * Exclui uma categoria de FAQ.
     * Obs: pode ser interessante validar se existem itens vinculados antes de excluir.
     */
    public void excluirCategoria(UUID categoriaId) {
        FaqCategoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        // Verificar se existem itens vinculados
        if (!itemRepository.findByCategoria(categoria).isEmpty()) {
            throw new IllegalStateException("Não é possível excluir a categoria pois existem itens vinculados.");
        }

        categoriaRepository.delete(categoria);
    }

    /**
     * Exclui um item de FAQ.
     */
    public void excluirItem(UUID itemId) {
        FaqItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));

        itemRepository.delete(item);
    }
}
