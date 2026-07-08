package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Repository.Auth.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdicionarEnderecoUseCase {

    @Autowired
    private EnderecoRepository enderecoRepository;

    /**
     * Adiciona um novo endereço para o usuário.
     */
    public Endereco adicionar(UUID usuarioId,
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
                              boolean isDefault) {

        // 1. Criar novo endereço
        Endereco endereco = new Endereco();
        endereco.setUsuarioId(usuarioId);
        endereco.setNomeRecebedor(nomeRecebedor);
        endereco.setTelefoneRecebedor(telefoneRecebedor);
        endereco.setCep(cep);
        endereco.setLogradouro(logradouro);
        endereco.setNumero(numero);
        endereco.setComplemento(complemento);
        endereco.setBairro(bairro);
        endereco.setCidade(cidade);
        endereco.setEstado(estado);
        endereco.setPais(pais != null ? pais : "Brasil");
        endereco.setIsDefault(isDefault);

        // 2. Se for endereço padrão, invalidar outros padrões
        if (isDefault) {
            enderecoRepository.findByUsuarioId(usuarioId).forEach(e -> {
                if (Boolean.TRUE.equals(e.getIsDefault())) {
                    e.setIsDefault(false);
                    enderecoRepository.save(e);
                }
            });
        }

        // 3. Persistir novo endereço
        return enderecoRepository.save(endereco);
    }
}
