package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Auth.Permissao;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Auth.PermissaoRepository;
import br.com.otica.otica_loja.enums.PermissaoNome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/admin/usuarios")
public class AdminCadastroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/criar-admin")
    public ResponseEntity<?> criarAdmin(@RequestBody Map<String, String> payload) {
        try {
            // 1. Busca a permissão ADMIN. Se não existir, cria e salva na hora.
            Permissao permissaoAdmin = permissaoRepository.findByNome(PermissaoNome.ADMIN)
                    .orElseGet(() -> {
                        Permissao novaPermissao = new Permissao();
                        novaPermissao.setNome(PermissaoNome.ADMIN);
                        novaPermissao.setDescricao("Acesso total ao painel administrativo");
                        novaPermissao.setAtivo(true);
                        novaPermissao.setCriadoEm(OffsetDateTime.now());
                        novaPermissao.setAtualizadoEm(OffsetDateTime.now());
                        return permissaoRepository.save(novaPermissao);
                    });

            // 2. Instancia o novo usuário com os dados do Postman
            Usuario novoAdmin = new Usuario();
            novoAdmin.setNome(payload.get("nome"));
            novoAdmin.setEmail(payload.get("email"));
            novoAdmin.setTelefone(payload.get("telefone"));

            // Criptografa a senha recebida do Postman
            String senhaCriptografada = passwordEncoder.encode(payload.get("senha"));
            novoAdmin.setSenhaHash(senhaCriptografada);

            novoAdmin.setAtivo(true);
            novoAdmin.setVerificado(true); // Já cria como verificado para testes
            novoAdmin.setCriadoEm(OffsetDateTime.now());
            novoAdmin.setAtualizadoEm(OffsetDateTime.now());

            // 3. Vincula a permissão de ADMIN
            novoAdmin.addPermissao(permissaoAdmin);

            // 4. Salva o usuário no banco
            usuarioRepository.save(novoAdmin);

            return ResponseEntity.ok("Administrador criado com sucesso e permissão configurada!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar admin: " + e.getMessage());
        }
    }
}