package br.com.otica.otica_loja.UseCases.auth;

import br.com.otica.otica_loja.Entity.Auth.Perfil;
import br.com.otica.otica_loja.Entity.Auth.Permissao;
import br.com.otica.otica_loja.Entity.Auth.TokenVerificacaoEmail;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.PerfilRepository;
import br.com.otica.otica_loja.Repository.Auth.PermissaoRepository;
import br.com.otica.otica_loja.Repository.Auth.TokenVerificacaoEmailRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.dto.auth.RegisterRequest;
import br.com.otica.otica_loja.enums.PermissaoNome;
import br.com.otica.otica_loja.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PermissaoRepository permissaoRepository;
    private final PerfilRepository perfilRepository;
    private final TokenVerificacaoEmailRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Registra um novo cliente no sistema:
     *  - valida e-mail único (apenas ativos)
     *  - valida CPF (se informado) contra Perfis existentes
     *  - cria Usuario + Perfil (1:1)
     *  - atribui permissão CLIENTE
     *  - salva Usuario (com cascade para Perfil)
     *  - gera token de verificação por e-mail e envia e-mail
     *
     * @param request dados de registro
     * @return usuário salvo (não verificado)
     */
    @Transactional
    public Usuario registrarCliente(RegisterRequest request) {
        // Normaliza e validações básicas
        String email = request.getEmail() == null ? null : request.getEmail().toLowerCase().trim();
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("E-mail é obrigatório.");
        }

        if (usuarioRepository.findByEmailAndAtivoTrue(email).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        // Valida CPF se fornecido
        String cpf = request.getCpf();
        if (cpf != null && !cpf.trim().isEmpty()) {
            if (perfilRepository.existsByCpf(cpf.trim())) {
                throw new IllegalArgumentException("CPF já cadastrado no sistema.");
            }
        }

        // Cria Usuario
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());
        usuario.setAtivo(true);
        usuario.setVerificado(false);

        // Cria Perfil e associa (1:1)
        Perfil perfil = new Perfil();
        perfil.setNome(request.getNome());
        perfil.setTelefone(request.getTelefone());
        perfil.setCpf(cpf == null ? null : cpf.trim());
        perfil.setUsuario(usuario);
        usuario.setPerfil(perfil);

        // Busca permissão CLIENTE e atribui
        Permissao permissaoCliente = permissaoRepository.findByNome(PermissaoNome.CLIENTE)
                .orElseThrow(() -> new IllegalStateException("Permissão CLIENTE não encontrada na base de dados."));
        usuario.addPermissao(permissaoCliente);

        // Salva usuário (com cascade para perfil)
        Usuario salvo = usuarioRepository.save(usuario);

        // Gera token de verificação de e-mail
        String tokenString = UUID.randomUUID().toString();
        TokenVerificacaoEmail token = new TokenVerificacaoEmail();
        token.setUsuarioId(salvo.getId());
        token.setToken(tokenString);
        token.setExpiraEm(OffsetDateTime.now().plusHours(24));
        token.setVerificado(false);

        tokenRepository.save(token);

        // Dispara e-mail de verificação (implementação do EmailService deve montar link com token)
        emailService.enviarEmailVerificacao(salvo.getEmail(), salvo.getNome(), tokenString);

        return salvo;
    }
}
