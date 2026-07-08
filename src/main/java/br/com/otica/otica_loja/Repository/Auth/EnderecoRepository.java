package br.com.otica.otica_loja.Repository.Auth;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {

    // Buscar todos os endereços de um usuário
    List<Endereco> findByUsuarioId(UUID usuarioId);

    // Buscar endereço padrão de um usuário
    Optional<Endereco> findByUsuarioIdAndIsDefaultTrue(UUID usuarioId);

    // Verificar se existe endereço padrão para o usuário
    boolean existsByUsuarioIdAndIsDefaultTrue(UUID usuarioId);

    // Buscar endereço pelo CEP
    List<Endereco> findByCep(String cep);
}
