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


            String clienteTelefone = usuario != null && usuario.getTelefone() != null
                    ? usuario.getTelefone() : "Não informado";

            List<ItemResumoDTO> itensDTO = pedido.getItens().stream()
                    .map(item -> new ItemResumoDTO(item.getNomeProduto(), item.getQuantidade()))
                    .toList();

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