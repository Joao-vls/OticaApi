package br.com.otica.otica_loja.Repository.Estoque;

import br.com.otica.otica_loja.Entity.Estoque.EstoqueMovimentacao;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.enums.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EstoqueMovimentacaoRepository extends JpaRepository<EstoqueMovimentacao, UUID> {

    // Buscar movimentações por variante
    List<EstoqueMovimentacao> findByVariante(ProdutoVariante variante);

    // Buscar movimentações por tipo (ENTRADA, SAÍDA, AJUSTE)
    List<EstoqueMovimentacao> findByTipo(TipoMovimentacao tipo);

    // Buscar movimentações de uma variante por tipo
    List<EstoqueMovimentacao> findByVarianteAndTipo(ProdutoVariante variante, TipoMovimentacao tipo);

    // Buscar movimentações realizadas por um usuário
    List<EstoqueMovimentacao> findByUsuarioId(UUID usuarioId);

    // Buscar movimentações criadas após uma data
    List<EstoqueMovimentacao> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar movimentações criadas antes de uma data
    List<EstoqueMovimentacao> findByCriadoEmBefore(OffsetDateTime data);

    // Buscar movimentações entre duas datas
    List<EstoqueMovimentacao> findByCriadoEmBetween(OffsetDateTime inicio, OffsetDateTime fim);
}
