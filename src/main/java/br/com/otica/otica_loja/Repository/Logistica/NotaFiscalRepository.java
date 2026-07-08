package br.com.otica.otica_loja.Repository.Logistica;

import br.com.otica.otica_loja.Entity.Logistica.NotaFiscal;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, UUID> {

    // Buscar nota fiscal pelo pedido
    Optional<NotaFiscal> findByPedido(Pedido pedido);

    // Buscar nota fiscal pelo número da NFe
    Optional<NotaFiscal> findByNumeroNfe(String numeroNfe);

    // Buscar nota fiscal pela chave de acesso
    Optional<NotaFiscal> findByChaveAcesso(String chaveAcesso);

    // Buscar notas fiscais emitidas após uma data
    List<NotaFiscal> findByEmitidaEmAfter(OffsetDateTime data);

    // Buscar notas fiscais emitidas antes de uma data
    List<NotaFiscal> findByEmitidaEmBefore(OffsetDateTime data);

    // Verificar se já existe nota fiscal para um pedido
    boolean existsByPedido(Pedido pedido);
}
