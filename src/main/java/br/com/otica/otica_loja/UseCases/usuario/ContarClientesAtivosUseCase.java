package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.enums.PermissaoNome; // IMPORT ADICIONADO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContarClientesAtivosUseCase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Long executar() {
        // Agora passando diretamente o Enum ao invés da String "CLIENTE"
        return usuarioRepository.countUsuariosAtivosByPermissao(PermissaoNome.CLIENTE);
    }
}