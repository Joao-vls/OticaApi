package br.com.otica.otica_loja.UseCases.pedidos;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Entity.Carrinho.CarrinhoItem;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Entity.Estoque.EstoqueMovimentacao;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.PedidoItem;
import br.com.otica.otica_loja.Repository.Auth.EnderecoRepository;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoItemRepository;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import br.com.otica.otica_loja.Repository.Estoque.EstoqueMovimentacaoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.TipoMovimentacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ProdutoVarianteRepository produtoVarianteRepository;

    @Autowired
    private EstoqueMovimentacaoRepository estoqueMovimentacaoRepository;

    @Transactional
    public Pedido criar(UUID usuarioId, String codigoCupom, BigDecimal valorFrete, String observacoes) {
        // 1. Buscar carrinho
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado."));

        List<CarrinhoItem> itensCarrinho = carrinho.getItens(); // Mudança sutil: acessando direto pela Entidade

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

        // 7. Criar itens do pedido e RESERVAR ESTOQUE
        for (CarrinhoItem itemCarrinho : itensCarrinho) {
            ProdutoVariante variante = itemCarrinho.getVariante();
            Produto produto = variante.getProduto();
            Integer quantidadeSolicitada = itemCarrinho.getQuantidade();


            // 7.1 Validação de Produto Ativo
            if (produto != null && !produto.getAtivo()) {
                throw new IllegalArgumentException(
                        String.format("O produto '%s' não está mais disponível para venda.", produto.getNome())
                );
            }

            if (!variante.getAtivo()) {
                throw new IllegalArgumentException(
                        String.format("A variação '%s' do produto '%s' não está mais disponível.",
                                variante.getNome(), produto != null ? produto.getNome() : "")
                );
            }

            // 7.2 Validação rigorosa de estoque
            if (variante.getStock() < quantidadeSolicitada) {
                throw new IllegalArgumentException(
                        String.format("Estoque insuficiente para o produto: %s (SKU: %s). Disponível: %d",
                                variante.getNome(), variante.getSku(), variante.getStock())
                );
            }

            // 7.3 Atualizar Saldo
            Integer saldoAnterior = variante.getStock();
            Integer saldoAtual = saldoAnterior - quantidadeSolicitada;
            variante.setStock(saldoAtual);
            produtoVarianteRepository.save(variante);

            // 7.4 Registrar movimentação de saída
            EstoqueMovimentacao movimentacao = new EstoqueMovimentacao();
            movimentacao.setVariante(variante);
            movimentacao.setTipo(TipoMovimentacao.SAIDA);
            movimentacao.setQuantidade(quantidadeSolicitada);
            movimentacao.setSaldoAnterior(saldoAnterior);
            movimentacao.setSaldoAtual(saldoAtual);
            movimentacao.setUsuarioId(usuarioId);
            movimentacao.setObservacao("Reserva de estoque - Pedido a aguardar pagamento.");
            movimentacao.setCriadoEm(OffsetDateTime.now());
            estoqueMovimentacaoRepository.save(movimentacao);

            // 7.5 Criar o item do pedido
            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setPedido(pedido);
            pedidoItem.setProduto(produto);
            pedidoItem.setVariante(variante);
            pedidoItem.setNomeProduto(variante.getNome());
            pedidoItem.setSku(variante.getSku());
            pedidoItem.setQuantidade(quantidadeSolicitada);
            pedidoItem.setPrecoUnitario(itemCarrinho.getPrecoUnitario());
            pedidoItem.setSubtotal(itemCarrinho.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(quantidadeSolicitada)));

            pedidoItemRepository.save(pedidoItem);
        }

        // 8. Remover itens do carrinho (A SOLUÇÃO AQUI 👇)
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);

        return pedido;
    }
}