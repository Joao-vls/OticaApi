package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Repository.Auth.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AtualizarEnderecoUseCase {

    @Autowired
    private EnderecoRepository enderecoRepository;

    /**
     * Atualiza os dados de um endereço existente.
     */
    public Endereco atualizar(UUID enderecoId,
                              String nomeRecebedor,
                              String telefoneRecebedor,
                              String cep,
                              String logradouro,
                              String numero,
                              String complemento,
                              String bairro,
                              String cidade,
                              String estado,
                              String pais,
                              Boolean isDefault) {

        // 1. Buscar endereço existente
        Optional<Endereco> enderecoOpt = enderecoRepository.findById(enderecoId);
        if (enderecoOpt.isEmpty()) {
            throw new IllegalArgumentException("Endereço não encontrado.");
        }

        Endereco endereco = enderecoOpt.get();

        // 2. Atualizar campos informados
        if (nomeRecebedor != null && !nomeRecebedor.isBlank()) {
            endereco.setNomeRecebedor(nomeRecebedor);
        }
        if (telefoneRecebedor != null && !telefoneRecebedor.isBlank()) {
            endereco.setTelefoneRecebedor(telefoneRecebedor);
        }
        if (cep != null && !cep.isBlank()) {
            endereco.setCep(cep);
        }
        if (logradouro != null && !logradouro.isBlank()) {
            endereco.setLogradouro(logradouro);
        }
        if (numero != null && !numero.isBlank()) {
            endereco.setNumero(numero);
        }
        if (complemento != null) {
            endereco.setComplemento(complemento);
        }
        if (bairro != null && !bairro.isBlank()) {
            endereco.setBairro(bairro);
        }
        if (cidade != null && !cidade.isBlank()) {
            endereco.setCidade(cidade);
        }
        if (estado != null && !estado.isBlank()) {
            endereco.setEstado(estado);
        }
        if (pais != null && !pais.isBlank()) {
            endereco.setPais(pais);
        }

        // 3. Atualizar flag de endereço padrão
        if (isDefault != null) {
            if (isDefault) {
                // Desmarcar outros endereços padrão do mesmo usuário
                enderecoRepository.findByUsuarioId(endereco.getUsuarioId()).forEach(e -> {
                    if (Boolean.TRUE.equals(e.getIsDefault())) {
                        e.setIsDefault(false);
                        enderecoRepository.save(e);
                    }
                });
            }
            endereco.setIsDefault(isDefault);
        }

        // 4. Atualizar timestamp
        endereco.setAtualizadoEm(OffsetDateTime.now());

        // 5. Persistir alterações
        return enderecoRepository.save(endereco);
    }
}
