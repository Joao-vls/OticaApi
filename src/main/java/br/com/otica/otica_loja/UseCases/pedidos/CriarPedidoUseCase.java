package br.com.otica.otica_loja.UseCases.pedidos;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Entity.Carrinho.CarrinhoItem;
import br.com.otica.otica_loja.Entity.Comercial.Cupom;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.PedidoItem;
import br.com.otica.otica_loja.Repository.Auth.EnderecoRepository;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoItemRepository;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;

import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CriarPedidoUseCase {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private CarrinhoItemRepository carrinhoItemRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private CupomRepository cupomRepository;

    /**
     * Cria um pedido a partir do carrinho do usuário.
     */
    public Pedido criar(UUID usuarioId, String codigoCupom, BigDecimal valorFrete, String observacoes) {
        // 1. Buscar carrinho
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado."));

        List<CarrinhoItem> itensCarrinho = carrinhoItemRepository.findByCarrinho(carrinho);
        if (itensCarrinho.isEmpty()) {
            throw new IllegalArgumentException("Carrinho está vazio.");
        }

        // 2. Buscar endereço padrão
        Endereco endereco = enderecoRepository.findByUsuarioIdAndIsDefaultTrue(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Endereço padrão não encontrado."));

        // 3. Calcular subtotal
        BigDecimal subtotal = itensCarrinho.stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Aplicar cupom (se houver)
        BigDecimal desconto = BigDecimal.ZERO;
        Cupom cupom = null;
        if (codigoCupom != null && !codigoCupom.isBlank()) {
            cupom = cupomRepository.findByCodigo(codigoCupom)
                    .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado."));

            switch (cupom.getTipo().toLowerCase()) {
                case "percentual":
                    desconto = subtotal.multiply(cupom.getValor().divide(BigDecimal.valueOf(100)));
                    break;
                case "fixo":
                    desconto = cupom.getValor();
                    break;
                case "frete":
                    desconto = valorFrete.min(cupom.getValor());
                    break;
            }
        }

        // 5. Calcular total
        BigDecimal total = subtotal.subtract(desconto).add(valorFrete);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        // 6. Criar pedido
        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuarioId);
        pedido.setEnderecoId(endereco.getId());
        pedido.setCupom(cupom);
        pedido.setSubtotal(subtotal);
        pedido.setDesconto(desconto);
        pedido.setFrete(valorFrete);
        pedido.setTotal(total);
        pedido.setObservacoes(observacoes);
        pedido.setCriadoEm(OffsetDateTime.now());
        pedido.setAtualizadoEm(OffsetDateTime.now());

        pedido = pedidoRepository.save(pedido);

        // 7. Criar itens do pedido
        for (CarrinhoItem itemCarrinho : itensCarrinho) {
            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setPedido(pedido);
            pedidoItem.setProduto(itemCarrinho.getVariante().getProduto());
            pedidoItem.setVariante(itemCarrinho.getVariante());
            pedidoItem.setNomeProduto(itemCarrinho.getVariante().getNome());
            pedidoItem.setSku(itemCarrinho.getVariante().getSku());
            pedidoItem.setQuantidade(itemCarrinho.getQuantidade());
            pedidoItem.setPrecoUnitario(itemCarrinho.getPrecoUnitario());
            pedidoItem.setSubtotal(itemCarrinho.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(itemCarrinho.getQuantidade())));

            pedidoItemRepository.save(pedidoItem);
        }

        // 8. Remover carrinho (opcional, se não quiser manter histórico)
        carrinhoRepository.deleteByUsuarioId(usuarioId);

        return pedido;
    }
}
