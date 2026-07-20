package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import br.com.otica.otica_loja.dto.dashboard.DiaVendaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObterAtividadeGridUseCase {

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    public List<DiaVendaDTO> obterDadosGrid(int ano) {
        // 1. Busca APENAS as vendas reais registradas no banco para o ano
        List<PedidoItemRepository.AtividadeVendaProjection> vendasBanco = pedidoItemRepository.obterAtividadeVendasPorAno(ano);

        // 2. Transforma direto na lista compacta de dias ativos (sem gerar dias vazios)
        return vendasBanco.stream()
                .map(venda -> {
                    DiaVendaDTO diaDto = new DiaVendaDTO();
                    diaDto.setData(venda.getData());
                    diaDto.setQuantidade(venda.getQuantidade());
                    diaDto.setClasseCor(definirClasseCor(venda.getQuantidade()));
                    diaDto.setProdutos(List.of(venda.getNomeProduto())); // Se agrupou por nome, vem o nome correto aqui
                    return diaDto;
                })
                .collect(Collectors.toList());
    }

    private String definirClasseCor(long quantidade) {
        if (quantidade <= 2) return "v-ate2";
        if (quantidade <= 5) return "v-ate5";
        if (quantidade <= 10) return "v-ate10";
        return "v-mais15";
    }
}