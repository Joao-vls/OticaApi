package br.com.otica.otica_loja.Repository.Auth;

import br.com.otica.otica_loja.Entity.Auth.Permissao;
import br.com.otica.otica_loja.enums.PermissaoNome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, UUID> {

    // Buscar permissão pelo nome (ADMIN, GERENTE, CLIENTE)
    Optional<Permissao> findByNome(PermissaoNome nome);

    // Verificar se existe permissão ativa com determinado nome
    boolean existsByNomeAndAtivoTrue(PermissaoNome nome);
}
