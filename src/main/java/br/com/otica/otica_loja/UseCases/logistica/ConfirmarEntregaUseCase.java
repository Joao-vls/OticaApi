package br.com.otica.otica_loja.UseCases.logistica;

import br.com.otica.otica_loja.Entity.Logistica.Envio;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Logistica.EnvioRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.StatusPedido;
import br.com.otica.otica_loja.enums.TipoEmail;
import br.com.otica.otica_loja.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ConfirmarEntregaUseCase {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private EmailService emailService;

    public Envio confirmarEntrega(UUID envioId, String localizacao, String observacao) {
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new IllegalArgumentException("Envio não encontrado."));

        Pedido pedido = envio.getPedido();

        if (pedido.getStatus() != StatusPedido.ENVIADO) {
            throw new IllegalArgumentException("Somente pedidos enviados podem ser confirmados como entregues.");
        }

        envio.setEntregueEm(OffsetDateTime.now());
        envioRepository.save(envio);

        pedido.setStatus(StatusPedido.ENTREGUE);
        pedido.setAtualizadoEm(OffsetDateTime.now());
        pedido.setObservacoes("Pedido entregue em " + localizacao +
                (observacao != null ? " | Obs: " + observacao : ""));
        Pedido pedidoSalvo = pedidoRepository.save(pedido);


        emailService.enviarEmailPedido(pedidoSalvo, TipoEmail.PEDIDO_ENTREGUE, null);

        return envio;
    }
}