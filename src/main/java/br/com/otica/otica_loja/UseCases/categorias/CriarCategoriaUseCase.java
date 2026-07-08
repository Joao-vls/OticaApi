package br.com.otica.otica_loja.UseCases.categorias;

import br.com.otica.otica_loja.Entity.Catalogo.Categoria;
import br.com.otica.otica_loja.Repository.Catalogo.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante!

import java.time.OffsetDateTime;

@Service
public class CriarCategoriaUseCase {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Cria uma nova categoria no catálogo de forma transacional e segura.
     */
    @Transactional
    public Categoria criar(String nome, String slug, String descricao) {

        // 1. Verificar se já existe categoria com o mesmo nome ou slug
        if (categoriaRepository.existsByNome(nome)) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome.");
        }
        if (categoriaRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Já existe uma categoria com este slug.");
        }

        // 2. Criar nova categoria (Deixe o @GeneratedValue cuidar do ID)
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setSlug(slug);
        categoria.setDescricao(descricao);
        categoria.setAtivo(true);
        categoria.setCriadoEm(OffsetDateTime.now());
        categoria.setAtualizadoEm(OffsetDateTime.now());

        // 3. Persistir no banco
        return categoriaRepository.save(categoria);
    }
}