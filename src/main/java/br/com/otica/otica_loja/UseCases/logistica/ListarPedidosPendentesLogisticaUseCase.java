package br.com.otica.otica_loja.UseCases.logistica;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.dto.ItemResumoDTO;
import br.com.otica.otica_loja.dto.PedidoPendenteLogisticaDTO;
import br.com.otica.otica_loja.enums.StatusPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarPedidosPendentesLogisticaUseCase {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<PedidoPendenteLogisticaDTO> executar() {
        // Busca apenas os pedidos que estão em separação
        List<Pedido> pedidos = pedidoRepository.findByStatus(StatusPedido.SEPARACAO);

        return pedidos.stream().map(pedido -> {
            Usuario usuario = usuarioRepository.findById(pedido.getUsuarioId()).orElse(null);
            String clienteNome = usuario != null ? usuario.getNome() : "Desconhecido";

            // Verifique se o método do seu usuário é getTelefone() ou getCelular()
            // Estou usando getTelefone() aqui. Se for getCelular(), mude abaixo.
            String clienteTelefone = (usuario != null && usuario.getTelefone() != null)
                    ? usuario.getTelefone() : "Não informado";

            // MAPEIA OS ITENS CORRETAMENTE
            List<ItemResumoDTO> itensDTO = pedido.getItens() != null ? pedido.getItens().stream()
                    .map(item -> new ItemResumoDTO(item.getNomeProduto(), item.getQuantidade()))
                    .toList() : List.of(); // Evita NullPointerException se itens for null

            return new PedidoPendenteLogisticaDTO(
                    pedido.getId(),
                    pedido.getNumero(),
                    clienteNome,
                    clienteTelefone,
                    pedido.getCriadoEm(),
                    itensDTO
            );
        }).toList();
    }
}