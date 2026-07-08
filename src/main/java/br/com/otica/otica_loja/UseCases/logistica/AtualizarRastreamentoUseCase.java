package br.com.otica.otica_loja.UseCases.logistica;

import br.com.otica.otica_loja.Entity.Logistica.Envio;
import br.com.otica.otica_loja.Entity.Logistica.EnvioEvento;
import br.com.otica.otica_loja.Repository.Logistica.EnvioEventoRepository;
import br.com.otica.otica_loja.Repository.Logistica.EnvioRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AtualizarRastreamentoUseCase {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private EnvioEventoRepository envioEventoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Adiciona um evento de rastreamento a um envio.
     */
    public EnvioEvento adicionarEvento(UUID envioId, String descricao, String localizacao) {
        // 1. Buscar envio
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new IllegalArgumentException("Envio não encontrado."));

        // 2. Criar evento de rastreamento
        EnvioEvento evento = new EnvioEvento();
        evento.setEnvio(envio);
        evento.setDescricao(descricao);
        evento.setLocalizacao(localizacao);
        evento.setCriadoEm(OffsetDateTime.now());

        // 3. Atualizar status do pedido se necessário
        Pedido pedido = envio.getPedido();
        if (descricao.toLowerCase().contains("entregue")) {
            pedido.setStatus(StatusPedido.ENTREGUE);
            pedido.setAtualizadoEm(OffsetDateTime.now());
            pedido.setObservacoes("Pedido marcado como entregue | Localização: " + localizacao);
            pedidoRepository.save(pedido);

            envio.setEntregueEm(OffsetDateTime.now());
            envioRepository.save(envio);
        }

        // 4. Persistir evento
        return envioEventoRepository.save(evento);
    }
}
    