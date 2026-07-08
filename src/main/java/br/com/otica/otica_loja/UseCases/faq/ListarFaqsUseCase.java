package br.com.otica.otica_loja.UseCases.faq;

import br.com.otica.otica_loja.Entity.FAQ.FaqCategoria;
import br.com.otica.otica_loja.Entity.FAQ.FaqItem;
import br.com.otica.otica_loja.Repository.FAQ.FaqCategoriaRepository;
import br.com.otica.otica_loja.Repository.FAQ.FaqItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ListarFaqsUseCase {

    @Autowired
    private FaqCategoriaRepository categoriaRepository;

    @Autowired
    private FaqItemRepository itemRepository;

    /**
     * Lista todas as categorias com seus itens ativos.
     */
    public Map<FaqCategoria, List<FaqItem>> listarFaqs() {
        // 1. Buscar categorias ativas ordenadas por ordem
        List<FaqCategoria> categorias = categoriaRepository.findAllByOrderByOrdemAsc();

        Map<FaqCategoria, List<FaqItem>> resultado = new LinkedHashMap<>();

        // 2. Para cada categoria, buscar itens ativos ordenados
        for (FaqCategoria categoria : categorias) {
            if (Boolean.TRUE.equals(categoria.getAtivo())) {
                List<FaqItem> itens = itemRepository.findByCategoriaOrderByOrdemAsc(categoria);
                // Filtrar apenas itens ativos
                itens.removeIf(item -> !Boolean.TRUE.equals(item.getAtivo()));
                resultado.put(categoria, itens);
            }
        }

        return resultado;
    }
}
