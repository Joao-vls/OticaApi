package br.com.otica.otica_loja.UseCases.categorias;

import br.com.otica.otica_loja.Entity.Catalogo.Categoria;
import br.com.otica.otica_loja.Repository.Catalogo.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante!

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExcluirCategoriaUseCase {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Exclui (soft delete) uma categoria pelo ID de forma segura.
     */
    @Transactional
    public void excluir(UUID categoriaId) {
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(categoriaId);
        if (categoriaOpt.isEmpty()) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }

        Categoria categoria = categoriaOpt.get();

        categoria.setAtivo(false);
        categoria.setAtualizadoEm(OffsetDateTime.now());

        categoriaRepository.save(categoria);
    }

    /**
     * Exclusão definitiva (hard delete).
     */
    @Transactional
    public void excluirDefinitivo(UUID categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }
        categoriaRepository.deleteById(categoriaId);
    }
}