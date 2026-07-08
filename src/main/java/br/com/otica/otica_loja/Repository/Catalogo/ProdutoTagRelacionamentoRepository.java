package br.com.otica.otica_loja.Repository.Catalogo;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoTagRelacionamento;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoTagId;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoTagRelacionamentoRepository extends JpaRepository<ProdutoTagRelacionamento, ProdutoTagId> {

    // Buscar todos os relacionamentos de um produto
    List<ProdutoTagRelacionamento> findByProduto(Produto produto);

    // Buscar todos os relacionamentos de uma tag
    List<ProdutoTagRelacionamento> findByTag(ProdutoTag tag);

    // Verificar se um produto já possui determinada tag
    boolean existsByProdutoAndTag(Produto produto, ProdutoTag tag);

    // Remover relacionamento específico entre produto e tag
    void deleteByProdutoAndTag(Produto produto, ProdutoTag tag);
}
