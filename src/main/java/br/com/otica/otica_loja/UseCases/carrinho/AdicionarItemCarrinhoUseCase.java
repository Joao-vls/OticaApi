package br.com.otica.otica_loja.UseCases.carrinho;

import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Entity.Carrinho.CarrinhoItem;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoItemRepository;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AdicionarItemCarrinhoUseCase {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private CarrinhoItemRepository carrinhoItemRepository;

    @Autowired
    private ProdutoVarianteRepository varianteRepository;

    /**
     * Adiciona uma variante de produto ao carrinho do usuário.
     */
    public CarrinhoItem adicionar(UUID usuarioId, UUID varianteId, Integer quantidade) {
        // 1. Buscar carrinho do usuário
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado para este usuário."));

        // 2. Buscar variante do produto
        ProdutoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new IllegalArgumentException("Variante de produto não encontrada."));

        // 3. Validar estoque
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida.");
        }
        if (variante.getStock() < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente para esta variante.");
        }

        // 4. Verificar se já existe item dessa variante no carrinho
        var itemExistente = carrinhoItemRepository.findByCarrinhoAndVariante(carrinho, variante);

        if (itemExistente.isPresent()) {
            // Atualiza quantidade
            CarrinhoItem item = itemExistente.get();
            int novaQuantidade = item.getQuantidade() + quantidade;

            if (variante.getStock() < novaQuantidade) {
                throw new IllegalArgumentException("Estoque insuficiente para aumentar a quantidade.");
            }

            item.setQuantidade(novaQuantidade);
            item.setPrecoUnitario(calcularPreco(variante));
            return carrinhoItemRepository.save(item);
        }

        // 5. Criar novo item
        CarrinhoItem novoItem = new CarrinhoItem();
        novoItem.setCarrinho(carrinho);
        novoItem.setVariante(variante);
        novoItem.setQuantidade(quantidade);
        novoItem.setPrecoUnitario(calcularPreco(variante));
        novoItem.setCriadoEm(OffsetDateTime.now());

        // 6. Persistir
        return carrinhoItemRepository.save(novoItem);
    }

    /**
     * Calcula o preço unitário da variante, considerando override.
     */
    private BigDecimal calcularPreco(ProdutoVariante variante) {
        if (variante.getPriceOverride() != null) {
            return variante.getPriceOverride();
        }
        // fallback: preço do produto principal
        return variante.getProduto().getPreco();
    }
}
