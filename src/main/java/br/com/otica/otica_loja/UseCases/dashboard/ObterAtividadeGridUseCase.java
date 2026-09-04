package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import br.com.otica.otica_loja.dto.dashboard.DiaVendaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObterAtividadeGridUseCase {

    @Autowired
    private PedidoItemRepository pedidoItemRepository; // Ou PedidoRepository, dependendo de onde colocou a query

    public List<DiaVendaDTO> obterDadosGrid(int ano) {
        // 1. Busca as vendas reais registradas no banco para o ano, baseadas no status do Pedido
        List<PedidoItemRepository.AtividadeVendaProjection> vendasBanco = pedidoItemRepository.obterAtividadeVendasPorAno(ano);

        // 2. Transforma na lista compacta de dias ativos e divide a string de produtos em lista
        return vendasBanco.stream()
                .map(venda -> {
                    DiaVendaDTO diaDto = new DiaVendaDTO();
                    diaDto.setData(venda.getData());
                    diaDto.setQuantidade(venda.getQuantidade());
                    diaDto.setClasseCor(definirClasseCor(venda.getQuantidade()));

                    // Converte a string "Óculos A, Lente B" em um List<String> real
                    if (venda.getNomeProduto() != null && !venda.getNomeProduto().isEmpty()) {
                        diaDto.setProdutos(Arrays.asList(venda.getNomeProduto().split(",\\s*")));
                    } else {
                        diaDto.setProdutos(Collections.emptyList());
                    }

                    return diaDto;
                })
                .collect(Collectors.toList());
    }

    private String definirClasseCor(long quantidade) {
        if (quantidade == 0) return "v-zero"; // Opcional: Caso precise renderizar dias vazios no front
        if (quantidade <= 2) return "v-ate2";
        if (quantidade <= 5) return "v-ate5";
        if (quantidade <= 10) return "v-ate10";
        return "v-mais15";
    }
}