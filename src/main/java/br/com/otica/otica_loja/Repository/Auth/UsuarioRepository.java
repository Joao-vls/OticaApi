package br.com.otica.otica_loja.Repository.Auth;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.enums.PermissaoNome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    // Buscar usuário pelo email
    Optional<Usuario> findByEmail(String email);

    // Verificar se já existe usuário com determinado email
    boolean existsByEmail(String email);


    //Verificar se já existe outro usuário com o mesmo email (excluindo o ID atual)
    boolean existsByEmailAndIdNot(String email, UUID id);

    // Buscar usuários ativos
    Optional<Usuario> findByEmailAndAtivoTrue(String email);

    // Repare que mudei "Object role" para "PermissaoNome role"
    @Query("SELECT COUNT(u) FROM Usuario u JOIN u.permissoes p WHERE u.ativo = true AND p.nome = :role")
    Long countUsuariosAtivosByPermissao(@Param("role") PermissaoNome role);

    @Query("SELECT u FROM Usuario u JOIN u.permissoes p WHERE u.ativo = true AND p.nome = :role")
    List<Usuario> findUsuariosAtivosByPermissao(@Param("role") PermissaoNome role);

}