package br.com.otica.otica_loja.Repository.Avaliacao;

import br.com.otica.otica_loja.Entity.Avaliacao.ProdutoAvaliacao;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProdutoAvaliacaoRepository extends JpaRepository<ProdutoAvaliacao, UUID> {

    // Buscar todas as avaliações de um produto
    List<ProdutoAvaliacao> findByProduto(Produto produto);

    // Buscar avaliações aprovadas de um produto
    List<ProdutoAvaliacao> findByProdutoAndAprovadoTrue(Produto produto);

    // Buscar avaliação de um usuário para um produto específico
    Optional<ProdutoAvaliacao> findByProdutoAndUsuarioId(Produto produto, UUID usuarioId);

    // Verificar se um usuário já avaliou um produto
    boolean existsByProdutoAndUsuarioId(Produto produto, UUID usuarioId);

    List<ProdutoAvaliacao> findByProdutoAndAprovadoFalse(Produto produto);

    // Buscar avaliações por nota
    List<ProdutoAvaliacao> findByProdutoAndNota(Produto produto, Integer nota);

    // Buscar avaliações pendentes de aprovação
    List<ProdutoAvaliacao> findByAprovadoFalse();
}
