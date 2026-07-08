package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Auth.Perfil;
import br.com.otica.otica_loja.Entity.Auth.Permissao;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Auth.PerfilRepository;
import br.com.otica.otica_loja.Repository.Auth.PermissaoRepository;
import br.com.otica.otica_loja.enums.PermissaoNome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class RegistrarUsuarioUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // injeta o encoder configurado

    /**
     * Registra um novo usuário com perfil e permissões.
     */
    public Usuario registrar(String email,
                             String nome,
                             String senha,
                             String telefone,
                             String username,
                             LocalDate dataNascimento,
                             String cpf,
                             Set<PermissaoNome> permissoes) {

        // 1. Verificar se já existe usuário com o mesmo email
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        // 2. Criar usuário
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail(email);
        usuario.setNome(nome);
        usuario.setSenhaHash(passwordEncoder.encode(senha)); // senha criptografada
        usuario.setTelefone(telefone);

        // 3. Criar perfil vinculado
        Perfil perfil = new Perfil();
        perfil.setId(usuario.getId()); // mesmo ID
        perfil.setUsuario(usuario);
        perfil.setUsername(username);
        perfil.setNome(nome);
        perfil.setTelefone(telefone);
        perfil.setDataNascimento(dataNascimento);
        perfil.setCpf(cpf);

        usuario.setPerfil(perfil);

        // 4. Adicionar permissões
        if (permissoes != null && !permissoes.isEmpty()) {
            for (PermissaoNome nomePermissao : permissoes) {
                Optional<Permissao> permissao = permissaoRepository.findByNome(nomePermissao);
                permissao.ifPresent(usuario::addPermissao);
            }
        }

        // 5. Persistir no banco
        usuarioRepository.save(usuario);
        perfilRepository.save(perfil);

        return usuario;
    }
}
