package br.com.otica.otica_loja.Repository.Carrinho;

import br.com.otica.otica_loja.Entity.Carrinho.CarrinhoItem;
import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarrinhoItemRepository extends JpaRepository<CarrinhoItem, UUID> {

    // Buscar todos os itens de um carrinho
    List<CarrinhoItem> findByCarrinho(Carrinho carrinho);

    // Buscar item específico de um carrinho pela variante
    Optional<CarrinhoItem> findByCarrinhoAndVariante(Carrinho carrinho, ProdutoVariante variante);

    // Verificar se já existe item de uma variante no carrinho
    boolean existsByCarrinhoAndVariante(Carrinho carrinho, ProdutoVariante variante);

    // Remover item específico de um carrinho
    void deleteByCarrinhoAndVariante(Carrinho carrinho, ProdutoVariante variante);
}
