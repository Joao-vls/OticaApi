package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Avaliacao.ProdutoAvaliacaoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.dto.UsuarioAdminResumoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ListarUsuariosAdminResumoUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoAvaliacaoRepository avaliacaoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<UsuarioAdminResumoDTO> executar() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream().map(usuario -> {
            // Verifica se o usuário possui alguma avaliação pendente (aprovado = false)
            boolean temPendente = avaliacaoRepository.existsByUsuarioIdAndAprovadoFalse(usuario.getId());

            // Busca a data do pedido mais recente do usuário
            OffsetDateTime ultimaCompra = pedidoRepository.findTopByUsuarioIdOrderByCriadoEmDesc(usuario.getId())
                    .map(Pedido::getCriadoEm)
                    .orElse(null);

            return new UsuarioAdminResumoDTO(
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getTelefone(),
                    usuario.getAtivo(),
                    usuario.getAtualizadoEm(),
                    temPendente,
                    ultimaCompra
            );
        }).toList();
    }
}