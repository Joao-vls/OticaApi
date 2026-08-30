package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Avaliacao.ProdutoAvaliacao;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.UseCases.avaliacoes.AprovarAvaliacaoUseCase;
import br.com.otica.otica_loja.UseCases.pedidos.ListarPedidosUsuarioUseCase;
import br.com.otica.otica_loja.UseCases.usuario.GerenciarUsuarioAdminUseCase;
import br.com.otica.otica_loja.UseCases.usuario.ListarUsuariosAdminResumoUseCase;
import br.com.otica.otica_loja.dto.UsuarioAdminResumoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class AdminUsuarioController {

    @Autowired
    private GerenciarUsuarioAdminUseCase gerenciarUsuarioAdminUseCase;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ListarPedidosUsuarioUseCase listarPedidosUsuarioUseCase;

    @Autowired
    private AprovarAvaliacaoUseCase aprovarAvaliacaoUseCase;

    // 1. Ver status e dados do usuário (email, telefone, última vez ativo)
    @GetMapping("/{usuarioId}")
    public ResponseEntity<Usuario> buscarDetalhesUsuario(@PathVariable UUID usuarioId) {
        Usuario usuario = gerenciarUsuarioAdminUseCase.buscarDetalhes(usuarioId);
        return ResponseEntity.ok(usuario);
    }

    // 2. Ver produtos/pedidos comprados pelo usuário
    @GetMapping("/{usuarioId}/pedidos")
    public ResponseEntity<List<Pedido>> listarPedidosDoUsuario(@PathVariable UUID usuarioId) {
        List<Pedido> pedidos = listarPedidosUsuarioUseCase.listarTodos(usuarioId);
        return ResponseEntity.ok(pedidos);
    }

    // 3. Ver avaliações feitas pelo usuário
    @GetMapping("/{usuarioId}/avaliacoes")
    public ResponseEntity<List<ProdutoAvaliacao>> listarAvaliacoesDoUsuario(@PathVariable UUID usuarioId) {
        List<ProdutoAvaliacao> avaliacoes = gerenciarUsuarioAdminUseCase.listarAvaliacoesPorUsuario(usuarioId);
        return ResponseEntity.ok(avaliacoes);
    }

    // 4. Aprovar uma avaliação específica
    @PatchMapping("/avaliacoes/{avaliacaoId}/aprovar")
    public ResponseEntity<ProdutoAvaliacao> aprovarAvaliacao(@PathVariable UUID avaliacaoId) {
        ProdutoAvaliacao avaliacaoAprovada = aprovarAvaliacaoUseCase.aprovar(avaliacaoId);
        return ResponseEntity.ok(avaliacaoAprovada);
    }

    // 5. Banir/Desativar conta do usuário
    @PatchMapping("/{usuarioId}/banir")
    public ResponseEntity<Usuario> banirUsuario(@PathVariable UUID usuarioId) {
        Usuario usuarioBanido = gerenciarUsuarioAdminUseCase.banirUsuario(usuarioId);
        return ResponseEntity.ok(usuarioBanido);
    }

    // 6. Reativar conta do usuário (Opcional, mas recomendado)
    @PatchMapping("/{usuarioId}/reativar")
    public ResponseEntity<Usuario> reativarUsuario(@PathVariable UUID usuarioId) {
        Usuario usuarioReativado = gerenciarUsuarioAdminUseCase.reativarUsuario(usuarioId);
        return ResponseEntity.ok(usuarioReativado);
    }
    @Autowired
    private ListarUsuariosAdminResumoUseCase listarUsuariosAdminResumoUseCase;

    @GetMapping
    public ResponseEntity<List<UsuarioAdminResumoDTO>> listarUsuarios() {
        List<UsuarioAdminResumoDTO> resumo = listarUsuariosAdminResumoUseCase.executar();
        return ResponseEntity.ok(resumo);
    }

}