package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Perfil;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.PerfilRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class AtualizarPerfilUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    /**
     * Atualiza os dados do perfil de um usuário.
     */
    public Perfil atualizar(UUID usuarioId,
                            String nome,
                            String telefone,
                            String username,
                            LocalDate dataNascimento,
                            String cpf) {

        // 1. Buscar usuário
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Buscar perfil associado
        Perfil perfil = usuario.getPerfil();
        if (perfil == null) {
            throw new IllegalStateException("Perfil não encontrado para este usuário.");
        }

        // 3. Atualizar dados
        if (nome != null && !nome.isBlank()) {
            usuario.setNome(nome);
            perfil.setNome(nome);
        }

        if (telefone != null && !telefone.isBlank()) {
            usuario.setTelefone(telefone);
            perfil.setTelefone(telefone);
        }

        if (username != null && !username.isBlank()) {
            perfil.setUsername(username);
        }

        if (dataNascimento != null) {
            perfil.setDataNascimento(dataNascimento);
        }

        if (cpf != null && !cpf.isBlank()) {
            perfil.setCpf(cpf);
        }

        // 4. Persistir alterações
        usuarioRepository.save(usuario);
        perfilRepository.save(perfil);

        return perfil;
    }
}