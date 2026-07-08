package br.com.otica.otica_loja.UseCases.marcas;

import br.com.otica.otica_loja.Entity.Catalogo.Marca;
import br.com.otica.otica_loja.Repository.Catalogo.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BuscarMarcaUseCase {

    @Autowired
    private MarcaRepository marcaRepository;

    /**
     * Busca uma marca pelo ID (UUID).
     */
    @Transactional(readOnly = true)
    public Marca buscarPorId(UUID id) {
        return marcaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marca não encontrada com o ID fornecido."));
    }

    /**
     * Busca uma marca pelo Slug.
     */
    @Transactional(readOnly = true)
    public Marca buscarPorSlug(String slug) {
        return marcaRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Marca não encontrada com o slug fornecido."));
    }
}