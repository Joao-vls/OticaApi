package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.dto.dashboard.ItemVendaDTO;
import br.com.otica.otica_loja.dto.dashboard.UltimaVendaDTO;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObterUltimasVendasUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<UltimaVendaDTO> executar(int limite) {
        // Ordena por data de criação (mais recentes primeiro)
        PageRequest pageRequest = PageRequest.of(0, limite, Sort.by(Sort.Direction.DESC, "criadoEm"));

        // Status que representam "Pagaram" ou "Foram Entregues"
        List<StatusPedido> statusValidos = Arrays.asList(StatusPedido.PAGO, StatusPedido.ENTREGUE);

        // Busca os pedidos usando o método que criaremos no PedidoRepository
        return pedidoRepository.findByStatusIn(statusValidos, pageRequest)
                .stream()
                .map(this::mapearParaDTO)
                .collect(Collectors.toList());
    }

    private UltimaVendaDTO mapearParaDTO(Pedido pedido) {
        // 1. Busca o nome do usuário (Priorizando o Perfil, igual você fazia no SQL)
        String nomeDoCliente = usuarioRepository.findById(pedido.getUsuarioId())
                .map(usuario -> {
                    if (usuario.getPerfil() != null && usuario.getPerfil().getNome() != null) {
                        return usuario.getPerfil().getNome();
                    }
                    return usuario.getNome();
                })
                .orElse("Cliente N/A");

        // 2. Busca e mapeia os itens do pedido (Usando o método que você JÁ TEM: findByPedido)
        List<ItemVendaDTO> itensDTO = pedidoItemRepository.findByPedido(pedido)
                .stream()
                .map(item -> new ItemVendaDTO(
                        item.getNomeProduto(),
                        item.getQuantidade(),
                        item.getPrecoUnitario()
                ))
                .collect(Collectors.toList());

        // 3. Monta e retorna o DTO final
        return new UltimaVendaDTO(
                pedido.getNumero(),
                pedido.getId() != null ? pedido.getId().toString() : null,
                nomeDoCliente,
                pedido.getTotal(),
                pedido.getStatus() != null ? pedido.getStatus().name() : "INDEFINIDO",
                pedido.getCriadoEm() != null ? pedido.getCriadoEm().format(FORMATTER) : "",
                itensDTO
        );
    }
}