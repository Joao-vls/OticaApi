package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Perfil;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.PerfilRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AlterarTelefoneUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Transactional
    public Perfil alterarTelefone(UUID usuarioId, String novoTelefone) {
        if (novoTelefone == null || novoTelefone.isBlank()) {
            throw new IllegalArgumentException("O telefone não pode ser vazio.");
        }

        String telefoneSanitizado = novoTelefone.trim();

        // 1. Buscar usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // 2. Buscar perfil associado
        Perfil perfil = usuario.getPerfil();
        if (perfil == null) {
            throw new IllegalStateException("Perfil não encontrado para este usuário.");
        }

        // 3. Sincronizar telefone em ambas as entidades
        usuario.setTelefone(telefoneSanitizado);
        perfil.setTelefone(telefoneSanitizado);

        usuarioRepository.save(usuario);
        return perfilRepository.save(perfil);
    }
}