package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Repository.CRM.CrmClienteRepository;
import br.com.otica.otica_loja.dto.dashboard.IndicadoresClientesDTO;
import br.com.otica.otica_loja.enums.NivelCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObterIndicadoresClientesUseCase {

    @Autowired
    private CrmClienteRepository clienteRepository;

    /**
     * Retorna indicadores consolidados sobre os clientes.
     */
    public IndicadoresClientesDTO obterIndicadores() {
        IndicadoresClientesDTO dto = new IndicadoresClientesDTO();

        // Total de clientes
        dto.setTotalClientes(clienteRepository.count());

        // Distribuição por nível
        dto.setClientesBronze(clienteRepository.findByNivel(NivelCliente.BRONZE).size());
        dto.setClientesPrata(clienteRepository.findByNivel(NivelCliente.PRATA).size());
        dto.setClientesOuro(clienteRepository.findByNivel(NivelCliente.OURO).size());
        dto.setClientesDiamante(clienteRepository.findByNivel(NivelCliente.DIAMANTE).size());

        // Clientes engajados (score > 70, por exemplo)
        dto.setClientesEngajados(clienteRepository.findByScoreGreaterThan(70).size());

        // Clientes inativos (último pedido há mais de 1 ano)
        dto.setClientesInativos(clienteRepository.findByUltimoPedidoEmBefore(
                java.time.OffsetDateTime.now().minusYears(1)
        ).size());

        return dto;
    }
}
