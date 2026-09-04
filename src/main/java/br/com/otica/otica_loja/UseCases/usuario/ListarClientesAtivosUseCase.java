package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.enums.PermissaoNome; // IMPORT ADICIONADO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarClientesAtivosUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> executar() {
        // Passando diretamente o Enum
        return usuarioRepository.findUsuariosAtivosByPermissao(PermissaoNome.CLIENTE);
    }
}