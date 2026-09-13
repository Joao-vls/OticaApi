package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Perfil;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.PerfilRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class AtualizarPerfilUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    /**
     * Atualiza os dados do perfil e do usuário associado.
     */
    @Transactional
    public Perfil atualizar(UUID usuarioId,
                            String nome,
                            String telefone,
                            String username,
                            LocalDate dataNascimento,
                            String cpf,
                            String genero) {

        // 1. Buscar usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // 2. Buscar perfil associado
        Perfil perfil = usuario.getPerfil();
        if (perfil == null) {
            throw new IllegalStateException("Perfil não encontrado para este usuário.");
        }

        // 3. Atualizar dados do Usuário e Perfil
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

        // 🔥 4. Validação e Tratamento do CPF
        if (cpf != null && !cpf.isBlank()) {
            // Remove máscara e deixa apenas números
            String cpfLimpo = cpf.replaceAll("\\D", "");

            if (!cpfLimpo.isEmpty()) {
                // Valida se o CPF já pertence a outro usuário cadastrado
                boolean cpfJaCadastrado = perfilRepository.existsByCpfAndUsuarioIdNot(cpfLimpo, usuarioId);
                if (cpfJaCadastrado) {
                    throw new IllegalArgumentException("Este CPF já está cadastrado em outra conta.Caso esse seja seu cpf entre em contato com o suporte");
                }
                perfil.setCpf(cpfLimpo);
            }
        }

        // ✅ 5. Atualizar gênero (masculino, feminino, outros)
        if (genero != null && !genero.isBlank()) {
            perfil.setGenero(genero);
        }

        // 6. Persistir alterações
        usuarioRepository.save(usuario);
        return perfilRepository.save(perfil);
    }
}