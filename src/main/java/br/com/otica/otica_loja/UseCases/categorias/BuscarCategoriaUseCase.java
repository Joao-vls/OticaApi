package br.com.otica.otica_loja.UseCases.categorias;

import br.com.otica.otica_loja.Entity.Catalogo.Categoria;
import br.com.otica.otica_loja.Repository.Catalogo.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BuscarCategoriaUseCase {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Busca categoria pelo ID.
     */
    public Categoria buscarPorId(UUID categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));
    }

    /**
     * Busca categoria pelo nome.
     */
    public Categoria buscarPorNome(String nome) {
        return categoriaRepository.findByNome(nome)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com este nome."));
    }

    /**
     * Busca categoria pelo slug.
     */
    public Categoria buscarPorSlug(String slug) {
        return categoriaRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com este slug."));
    }

    /**
     * Lista todas as categorias ativas.
     */
    public List<Categoria> listarAtivas() {
        return categoriaRepository.findByAtivoTrue();
    }

    /**
     * Lista todas as categorias inativas.
     */
    public List<Categoria> listarInativas() {
        return categoriaRepository.findByAtivoFalse();
    }
}
