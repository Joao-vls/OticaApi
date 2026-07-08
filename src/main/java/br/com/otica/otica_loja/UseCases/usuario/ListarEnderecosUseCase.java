package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Repository.Auth.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarEnderecosUseCase {

    @Autowired
    private EnderecoRepository enderecoRepository;

    /**
     * Lista todos os endereços de um usuário.
     */
    public List<Endereco> listarPorUsuario(UUID usuarioId) {
        return enderecoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Lista endereços por CEP.
     */
    public List<Endereco> listarPorCep(String cep) {
        return enderecoRepository.findByCep(cep);
    }

    /**
     * Busca o endereço padrão de um usuário.
     */
    public Endereco buscarEnderecoPadrao(UUID usuarioId) {
        return enderecoRepository.findByUsuarioIdAndIsDefaultTrue(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Endereço padrão não encontrado."));
    }
}
