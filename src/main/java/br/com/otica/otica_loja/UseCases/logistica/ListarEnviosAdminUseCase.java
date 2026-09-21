package br.com.otica.otica_loja.UseCases.logistica;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Logistica.Envio;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Logistica.EnvioRepository;
import br.com.otica.otica_loja.dto.EnvioResumoDTO;
import br.com.otica.otica_loja.dto.ItemResumoDTO;
import br.com.otica.otica_loja.enums.StatusPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarEnviosAdminUseCase {

    private final EnvioRepository envioRepository;
    private final UsuarioRepository usuarioRepository;

    public List<EnvioResumoDTO> listarTodos() {
        List<Envio> envios = envioRepository.findAll();
        return mapearParaDTO(envios);
    }

    public List<EnvioResumoDTO> listarPorStatus(StatusPedido status) {
        List<Envio> envios = envioRepository.findByPedidoStatus(status);
        return mapearParaDTO(envios);
    }

    private List<EnvioResumoDTO> mapearParaDTO(List<Envio> envios) {
        return envios.stream().map(envio -> {

            Usuario cliente = usuarioRepository.findById(envio.getPedido().getUsuarioId()).orElse(null);

            String clienteNome = cliente != null ? cliente.getNome() : "Usuário Excluído/Desconhecido";
            String clienteEmail = cliente != null ? cliente.getEmail() : "N/A";
            String transportadoraNome = envio.getTransportadora() != null ? envio.getTransportadora().getNome() : "N/A";

            // 🎯 NOVO: Pega os itens do pedido associado a este envio
            List<ItemResumoDTO> itensDTO = envio.getPedido().getItens() != null
                    ? envio.getPedido().getItens().stream()
                    .map(item -> new ItemResumoDTO(item.getNomeProduto(), item.getQuantidade()))
                    .toList()
                    : List.of();

            return new EnvioResumoDTO(
                    envio.getId(),
                    envio.getCodigoRastreio(),
                    transportadoraNome,
                    envio.getPedido().getNumero(),
                    envio.getPedido().getStatus(),
                    clienteNome,
                    clienteEmail,
                    envio.getEnviadoEm(),
                    envio.getEntregueEm(),
                    itensDTO
            );
        }).toList();
    }
}