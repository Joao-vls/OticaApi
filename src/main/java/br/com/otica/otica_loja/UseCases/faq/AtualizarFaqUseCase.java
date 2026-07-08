package br.com.otica.otica_loja.UseCases.faq;

import br.com.otica.otica_loja.Entity.FAQ.FaqCategoria;
import br.com.otica.otica_loja.Entity.FAQ.FaqItem;
import br.com.otica.otica_loja.Repository.FAQ.FaqCategoriaRepository;
import br.com.otica.otica_loja.Repository.FAQ.FaqItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AtualizarFaqUseCase {

    @Autowired
    private FaqCategoriaRepository categoriaRepository;

    @Autowired
    private FaqItemRepository itemRepository;

    /**
     * Atualiza uma categoria de FAQ existente.
     */
    public FaqCategoria atualizarCategoria(UUID categoriaId, String novoTitulo, String novaDescricao, Boolean ativo, Integer ordem) {
        FaqCategoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        if (novoTitulo != null) categoria.setTitulo(novoTitulo);
        if (novaDescricao != null) categoria.setDescricao(novaDescricao);
        if (ativo != null) categoria.setAtivo(ativo);
        if (ordem != null) categoria.setOrdem(ordem);

        categoria.setAtualizadoEm(OffsetDateTime.now());

        return categoriaRepository.save(categoria);
    }

    /**
     * Atualiza um item de FAQ existente.
     */
    public FaqItem atualizarItem(UUID itemId, String novaPergunta, String novaResposta, Boolean ativo, Integer ordem) {
        FaqItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));

        if (novaPergunta != null) item.setPergunta(novaPergunta);
        if (novaResposta != null) item.setResposta(novaResposta);
        if (ativo != null) item.setAtivo(ativo);
        if (ordem != null) item.setOrdem(ordem);

        item.setAtualizadoEm(OffsetDateTime.now());

        return itemRepository.save(item);
    }
}
