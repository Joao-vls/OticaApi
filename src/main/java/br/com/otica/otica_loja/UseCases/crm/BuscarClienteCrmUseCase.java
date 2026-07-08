package br.com.otica.otica_loja.UseCases.crm;

import br.com.otica.otica_loja.Entity.CRM.CrmCliente;
import br.com.otica.otica_loja.Repository.CRM.CrmClienteRepository;
import br.com.otica.otica_loja.enums.NivelCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BuscarClienteCrmUseCase {

    @Autowired
    private CrmClienteRepository clienteRepository;

    /**
     * Buscar cliente CRM por usuárioId.
     */
    public CrmCliente buscarPorUsuarioId(UUID usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente CRM não encontrado."));
    }

    /**
     * Listar clientes por nível (Bronze, Prata, Ouro, Diamante).
     */
    public List<CrmCliente> listarPorNivel(NivelCliente nivel) {
        return clienteRepository.findByNivel(nivel);
    }

    /**
     * Listar clientes com score acima de um valor.
     */
    public List<CrmCliente> listarPorScoreMaiorQue(Integer score) {
        return clienteRepository.findByScoreGreaterThan(score);
    }

    /**
     * Listar clientes com valor gasto acima de um limite.
     */
    public List<CrmCliente> listarPorValorGastoMaiorQue(BigDecimal valor) {
        return clienteRepository.findByValorGastoGreaterThan(valor);
    }

    /**
     * Listar clientes com total de pedidos acima de um limite.
     */
    public List<CrmCliente> listarPorTotalPedidosMaiorQue(Integer totalPedidos) {
        return clienteRepository.findByTotalPedidosGreaterThan(totalPedidos);
    }

    /**
     * Listar clientes que fizeram pedido recentemente.
     */
    public List<CrmCliente> listarPorUltimoPedidoDepoisDe(OffsetDateTime data) {
        return clienteRepository.findByUltimoPedidoEmAfter(data);
    }

    /**
     * Listar clientes que não fazem pedido há muito tempo.
     */
    public List<CrmCliente> listarPorUltimoPedidoAntesDe(OffsetDateTime data) {
        return clienteRepository.findByUltimoPedidoEmBefore(data);
    }
}
