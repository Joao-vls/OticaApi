package br.com.otica.otica_loja.Repository.Pedidos;

import br.com.otica.otica_loja.Entity.Pedidos.TrocaDevolucao;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.enums.StatusTrocaDevolucao;
import br.com.otica.otica_loja.enums.TipoTrocaDevolucao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrocaDevolucaoRepository extends JpaRepository<TrocaDevolucao, UUID> {

    // Buscar trocas/devoluções de um pedido específico
    List<TrocaDevolucao> findByPedido(Pedido pedido);

    // Buscar trocas/devoluções de um usuário
    List<TrocaDevolucao> findByUsuarioId(UUID usuarioId);

    // Buscar por tipo (TROCA ou DEVOLUCAO)
    List<TrocaDevolucao> findByTipo(TipoTrocaDevolucao tipo);

    // Buscar por status (SOLICITADO, EM_ANALISE, APROVADO, REJEITADO, CONCLUIDO)
    List<TrocaDevolucao> findByStatus(StatusTrocaDevolucao status);

    // Buscar trocas/devoluções criadas após uma data
    List<TrocaDevolucao> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar trocas/devoluções atualizadas após uma data
    List<TrocaDevolucao> findByAtualizadoEmAfter(OffsetDateTime data);

    // Buscar trocas/devoluções por motivo (texto parcial)
    List<TrocaDevolucao> findByMotivoContainingIgnoreCase(String motivo);
}
