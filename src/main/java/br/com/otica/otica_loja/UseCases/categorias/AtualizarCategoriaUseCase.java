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
public class AtualizarCategoriaUseCase {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Atualiza os dados de uma categoria existente de forma segura contra Lock Otimista.
     */
    @Transactional // 1. Garante o isolamento da transação do início ao fim
    public Categoria atualizar(UUID categoriaId,
                               String nome,
                               String slug,
                               String descricao,
                               Boolean ativo) {

        // 1. Buscar categoria existente
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(categoriaId);
        if (categoriaOpt.isEmpty()) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }

        Categoria categoria = categoriaOpt.get();

        // =========================================================================
        // 2. ETAPA DE VALIDAÇÃO: Executa consultas no banco ANTES de alterar o objeto
        // =========================================================================
        if (nome != null && !nome.isBlank() && !categoria.getNome().equalsIgnoreCase(nome)) {
            if (categoriaRepository.existsByNome(nome)) {
                throw new IllegalArgumentException("Já existe uma categoria com este nome.");
            }
        }

        if (slug != null && !slug.isBlank() && !categoria.getSlug().equalsIgnoreCase(slug)) {
            if (categoriaRepository.existsBySlug(slug)) {
                throw new IllegalArgumentException("Já existe uma categoria com este slug.");
            }
        }

        // =========================================================================
        // 3. ETAPA DE ALTERAÇÃO: Atualiza os campos apenas após passar nos testes
        // =========================================================================
        if (nome != null && !nome.isBlank()) {
            categoria.setNome(nome);
        }

        if (slug != null && !slug.isBlank()) {
            categoria.setSlug(slug);
        }

        if (descricao != null) {
            categoria.setDescricao(descricao);
        }

        if (ativo != null) {
            categoria.setAtivo(ativo);
        }

        // 4. Atualizar timestamp
        categoria.setAtualizadoEm(OffsetDateTime.now());

        // 5. Persistir alterações com segurança
        return categoriaRepository.save(categoria);
    }
}