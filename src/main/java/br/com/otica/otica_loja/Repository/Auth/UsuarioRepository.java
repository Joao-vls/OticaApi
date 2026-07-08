package br.com.otica.otica_loja.Repository.Auth;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    // Buscar usuário pelo email
    Optional<Usuario> findByEmail(String email);

    // Verificar se já existe usuário com determinado email
    boolean existsByEmail(String email);

    // Buscar usuários ativos
    Optional<Usuario> findByEmailAndAtivoTrue(String email);
}
