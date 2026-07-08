package br.com.otica.otica_loja.Repository.Vendas;

import br.com.otica.otica_loja.Entity.Vendas.Garantia;
import br.com.otica.otica_loja.Entity.Pedidos.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GarantiaRepository extends JpaRepository<Garantia, UUID> {

    // Buscar garantia de um item de pedido específico
    Optional<Garantia> findByPedidoItem(PedidoItem pedidoItem);

    // Buscar garantias ativas
    List<Garantia> findByAtivoTrue();

    // Buscar garantias inativas
    List<Garantia> findByAtivoFalse();

    // Buscar garantias que ainda estão válidas (dataFim após hoje)
    List<Garantia> findByDataFimAfter(LocalDate hoje);

    // Buscar garantias que já expiraram (dataFim antes de hoje)
    List<Garantia> findByDataFimBefore(LocalDate hoje);

    // Buscar garantias criadas após uma data
    List<Garantia> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar garantias criadas antes de uma data
    List<Garantia> findByCriadoEmBefore(OffsetDateTime data);
}
