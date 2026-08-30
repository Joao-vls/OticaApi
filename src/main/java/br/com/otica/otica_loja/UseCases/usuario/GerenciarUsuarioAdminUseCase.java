package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Avaliacao.ProdutoAvaliacao;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Avaliacao.ProdutoAvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GerenciarUsuarioAdminUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoAvaliacaoRepository avaliacaoRepository;

    /**
     * Busca os dados completos do usuário (traz o Perfil junto devido ao mapeamento JPA)
     */
    public Usuario buscarDetalhes(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    /**
     * Bane a conta do usuário (desativa)
     */
    public Usuario banirUsuario(UUID usuarioId) {
        Usuario usuario = buscarDetalhes(usuarioId);

        if (!usuario.getAtivo()) {
            throw new IllegalArgumentException("Este usuário já está banido/desativado.");
        }

        usuario.setAtivo(false);
        usuario.setAtualizadoEm(OffsetDateTime.now());

        return usuarioRepository.save(usuario);
    }

    /**
     * Reativa a conta de um usuário banido
     */
    public Usuario reativarUsuario(UUID usuarioId) {
        Usuario usuario = buscarDetalhes(usuarioId);

        if (usuario.getAtivo()) {
            throw new IllegalArgumentException("Este usuário já está ativo.");
        }

        usuario.setAtivo(true);
        usuario.setAtualizadoEm(OffsetDateTime.now());

        return usuarioRepository.save(usuario);
    }

    /**
     * Lista todas as avaliações feitas por um usuário específico
     */
    public List<ProdutoAvaliacao> listarAvaliacoesPorUsuario(UUID usuarioId) {
        // Requer que o ProdutoAvaliacaoRepository tenha o método findByUsuarioId(UUID usuarioId)
        return avaliacaoRepository.findByUsuarioId(usuarioId);
    }
}