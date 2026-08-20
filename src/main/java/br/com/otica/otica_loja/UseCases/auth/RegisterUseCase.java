package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.Perfil;
import br.com.otica.otica_loja.Entity.Auth.Permissao;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.PermissaoRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.dto.auth.RegisterRequest;
import br.com.otica.otica_loja.enums.PermissaoNome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrarCliente(RegisterRequest request) {
        // 1. Valida se o e-mail já está cadastrado
        if (usuarioRepository.findByEmailAndAtivoTrue(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        // 2. Cria a entidade Usuario
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail().toLowerCase().trim());
        usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());
        usuario.setAtivo(true);
        usuario.setVerificado(false);

        // 3. Cria e associa o Perfil (1:1)
        Perfil perfil = new Perfil();
        perfil.setNome(request.getNome());
        perfil.setTelefone(request.getTelefone());
        perfil.setCpf(request.getCpf());
        perfil.setUsuario(usuario);
        usuario.setPerfil(perfil);

        // 4. Busca a permissão CLIENTE e atribui ao usuário
        Permissao permissaoCliente = permissaoRepository.findByNome(PermissaoNome.CLIENTE)
                .orElseThrow(() -> new IllegalStateException("Permissão CLIENTE não encontrada na base de dados."));

        usuario.addPermissao(permissaoCliente);

        // 5. Salva o usuário no banco (com perfil via CascadeType.ALL)
        return usuarioRepository.save(usuario);
    }
}