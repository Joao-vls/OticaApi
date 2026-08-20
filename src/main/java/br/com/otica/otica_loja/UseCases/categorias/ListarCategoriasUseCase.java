package br.com.otica.otica_loja.UseCases.categorias;

import br.com.otica.otica_loja.Repository.Catalogo.CategoriaRepository;
import br.com.otica.otica_loja.dto.CategoriaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarCategoriasUseCase {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Lista todas as categorias.
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(CategoriaResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Lista todas as categorias ativas.
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarAtivas() {
        return categoriaRepository.findByAtivoTrue()
                .stream()
                .map(CategoriaResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Lista todas as categorias inativas.
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarInativas() {
        return categoriaRepository.findByAtivoFalse()
                .stream()
                .map(CategoriaResponseDTO::fromEntity)
                .toList();
    }
}