package br.com.otica.otica_loja.UseCases.logistica;

import br.com.otica.otica_loja.Entity.Logistica.Envio;
import br.com.otica.otica_loja.Entity.Logistica.Transportadora;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Logistica.EnvioRepository;
import br.com.otica.otica_loja.Repository.Logistica.TransportadoraRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CriarEnvioUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private TransportadoraRepository transportadoraRepository;

    @Autowired
    private EnvioRepository envioRepository;

    /**
     * Cria um envio para um pedido e atualiza o status do pedido para ENVIADO.
     */
    public Envio criarEnvio(UUID pedidoId, UUID transportadoraId, String codigoRastreio, String urlRastreio, BigDecimal valorFrete, String observacao) {
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.SEPARACAO) {
            throw new IllegalArgumentException("Pedido precisa estar em separação para ser enviado.");
        }

        // 2. Buscar transportadora
        Transportadora transportadora = transportadoraRepository.findById(transportadoraId)
                .orElseThrow(() -> new IllegalArgumentException("Transportadora não encontrada."));

        // 3. Criar envio
        Envio envio = new Envio();
        envio.setPedido(pedido);
        envio.setTransportadora(transportadora);
        envio.setCodigoRastreio(codigoRastreio);
        envio.setUrlRastreio(urlRastreio);
        envio.setValorFrete(valorFrete);
        envio.setCriadoEm(OffsetDateTime.now());
        envio.setEnviadoEm(OffsetDateTime.now());

        // 4. Atualizar pedido para ENVIADO
        pedido.setStatus(StatusPedido.ENVIADO);
        pedido.setAtualizadoEm(OffsetDateTime.now());
        pedido.setObservacoes("Pedido enviado pela transportadora " + transportadora.getNome() +
                " | Código de rastreio: " + codigoRastreio +
                (observacao != null ? " | Obs: " + observacao : ""));

        // 5. Persistir alterações
        pedidoRepository.save(pedido);
        return envioRepository.save(envio);
    }
}
