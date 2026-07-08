package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Auth.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ExcluirContaUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    /**
     * Exclui a conta de um usuário e seu perfil associado.
     */
    public void excluir(UUID usuarioId) {
        // 1. Buscar usuário
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Excluir perfil associado (se existir)
        if (usuario.getPerfil() != null) {
            perfilRepository.delete(usuario.getPerfil());
        }

        // 3. Excluir usuário
        usuarioRepository.delete(usuario);
    }
}
