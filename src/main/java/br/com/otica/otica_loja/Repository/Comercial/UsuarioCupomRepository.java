package br.com.otica.otica_loja.Repository.Comercial;

import br.com.otica.otica_loja.Entity.Comercial.UsuarioCupom;
import br.com.otica.otica_loja.Entity.Comercial.UsuarioCupomId;
import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioCupomRepository extends JpaRepository<UsuarioCupom, UsuarioCupomId> {

    // Buscar todos os cupons utilizados por um usuário
    List<UsuarioCupom> findByUsuario(Usuario usuario);

    // Buscar todos os usuários que utilizaram um cupom específico
    List<UsuarioCupom> findByCupom(Cupom cupom);

    // Verificar se um usuário já utilizou determinado cupom
    boolean existsByUsuarioAndCupom(Usuario usuario, Cupom cupom);

    // Buscar relacionamento específico entre usuário e cupom
    Optional<UsuarioCupom> findByUsuarioAndCupom(Usuario usuario, Cupom cupom);

    // Remover relacionamento específico entre usuário e cupom
    void deleteByUsuarioAndCupom(Usuario usuario, Cupom cupom);
}
