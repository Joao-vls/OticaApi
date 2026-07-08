package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Repository.Auth.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RemoverEnderecoUseCase {

    @Autowired
    private EnderecoRepository enderecoRepository;

    /**
     * Remove um endereço pelo ID.
     */
    public void remover(UUID enderecoId) {
        // 1. Buscar endereço
        Optional<Endereco> enderecoOpt = enderecoRepository.findById(enderecoId);
        if (enderecoOpt.isEmpty()) {
            throw new IllegalArgumentException("Endereço não encontrado.");
        }

        Endereco endereco = enderecoOpt.get();

        // 2. Excluir endereço
        enderecoRepository.delete(endereco);
    }
}
