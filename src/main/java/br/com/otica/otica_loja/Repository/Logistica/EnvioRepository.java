package br.com.otica.otica_loja.Repository.Logistica;

import br.com.otica.otica_loja.Entity.Logistica.Envio;
import br.com.otica.otica_loja.Entity.Logistica.Transportadora;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, UUID> {

    // Buscar envio pelo pedido
    Optional<Envio> findByPedido(Pedido pedido);

    // Buscar envios por transportadora
    List<Envio> findByTransportadora(Transportadora transportadora);

    // Buscar envio pelo código de rastreio
    Optional<Envio> findByCodigoRastreio(String codigoRastreio);

    // Buscar envios enviados após uma data
    List<Envio> findByEnviadoEmAfter(OffsetDateTime data);

    // Buscar envios entregues após uma data
    List<Envio> findByEntregueEmAfter(OffsetDateTime data);

    // Buscar envios ainda não entregues
    List<Envio> findByEntregueEmIsNull();

    // Buscar envios criados após uma data
    List<Envio> findByCriadoEmAfter(OffsetDateTime data);
}
