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
public class CriarFaqUseCase {

    @Autowired
    private FaqCategoriaRepository categoriaRepository;

    @Autowired
    private FaqItemRepository itemRepository;

    /**
     * Cria uma nova categoria de FAQ.
     */
    public FaqCategoria criarCategoria(String titulo, String descricao, Integer ordem) {
        if (categoriaRepository.findByTitulo(titulo).isPresent()) {
            throw new IllegalArgumentException("Já existe uma categoria com este título.");
        }

        FaqCategoria categoria = new FaqCategoria();
        categoria.setTitulo(titulo);
        categoria.setDescricao(descricao);
        categoria.setOrdem(ordem != null ? ordem : 0);
        categoria.setAtivo(true);
        categoria.setCriadoEm(OffsetDateTime.now());
        categoria.setAtualizadoEm(OffsetDateTime.now());

        return categoriaRepository.save(categoria);
    }

    /**
     * Cria um novo item de FAQ dentro de uma categoria existente.
     */
    public FaqItem criarItem(UUID categoriaId, String pergunta, String resposta, Integer ordem) {
        FaqCategoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        FaqItem item = new FaqItem();
        item.setCategoria(categoria);
        item.setPergunta(pergunta);
        item.setResposta(resposta);
        item.setOrdem(ordem != null ? ordem : 0);
        item.setAtivo(true);
        item.setVisualizacoes(0L);
        item.setCriadoEm(OffsetDateTime.now());
        item.setAtualizadoEm(OffsetDateTime.now());

        return itemRepository.save(item);
    }
}
