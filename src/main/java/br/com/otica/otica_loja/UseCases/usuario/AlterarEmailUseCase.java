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
public class AlterarEmailUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Transactional
    public Usuario alterarEmail(UUID usuarioId, String novoEmail) {
        if (novoEmail == null || novoEmail.isBlank()) {
            throw new IllegalArgumentException("O e-mail não pode ser vazio.");
        }

        String emailSanitizado = novoEmail.trim().toLowerCase();

        // 1. Buscar usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // 2. Verificar se o e-mail já pertence a outro usuário
        if (usuarioRepository.existsByEmailAndIdNot(emailSanitizado, usuarioId)) {
            throw new IllegalArgumentException("Este e-mail já está em uso por outra conta.");
        }

        // 3. Atualizar e-mail no Usuario
        usuario.setEmail(emailSanitizado);

        return usuarioRepository.save(usuario);
    }
}