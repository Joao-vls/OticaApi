package br.com.otica.otica_loja.Repository.Carrinho;

import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, UUID> {

    // Buscar carrinho de um usuário
    Optional<Carrinho> findByUsuarioId(UUID usuarioId);

    // Verificar se já existe carrinho para o usuário
    boolean existsByUsuarioId(UUID usuarioId);

    // Remover carrinho de um usuário
    void deleteByUsuarioId(UUID usuarioId);
}
