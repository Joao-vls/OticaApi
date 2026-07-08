package br.com.otica.otica_loja.UseCases.categorias;

import br.com.otica.otica_loja.Entity.Catalogo.Categoria;
import br.com.otica.otica_loja.Repository.Catalogo.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarCategoriasUseCase {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Lista todas as categorias.
     */
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
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
