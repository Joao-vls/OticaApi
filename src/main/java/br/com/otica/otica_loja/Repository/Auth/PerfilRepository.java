package br.com.otica.otica_loja.Repository.Auth;

import br.com.otica.otica_loja.Entity.Auth.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, UUID> {

    // Buscar perfil pelo username
    Optional<Perfil> findByUsername(String username);

    // Verificar se já existe username
    boolean existsByUsername(String username);

    // Buscar perfil pelo CPF
    Optional<Perfil> findByCpf(String cpf);

    // Buscar perfil ativo pelo usuário
    Optional<Perfil> findByUsuarioIdAndAtivoTrue(UUID usuarioId);
}
