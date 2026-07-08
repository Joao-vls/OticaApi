package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Perfil;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.PerfilRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BuscarPerfilUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    /**
     * Busca perfil pelo ID do usuário.
     */
    public Perfil buscarPorUsuarioId(UUID usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        Perfil perfil = usuario.getPerfil();

        if (perfil == null) {
            throw new IllegalStateException("Perfil não encontrado para este usuário.");
        }

        return perfil;
    }

    /**
     * Busca perfil pelo email do usuário.
     */
    public Perfil buscarPorEmail(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        Perfil perfil = usuario.getPerfil();

        if (perfil == null) {
            throw new IllegalStateException("Perfil não encontrado para este usuário.");
        }

        return perfil;
    }
}
