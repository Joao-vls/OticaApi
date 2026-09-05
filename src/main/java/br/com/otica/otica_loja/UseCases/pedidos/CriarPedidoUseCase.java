package br.com.otica.otica_loja.UseCases.pedidos;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Entity.Carrinho.CarrinhoItem;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
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
import br.com.otica.otica_loja.enums.TipoMidia;
import br.com.otica.otica_loja.enums.TipoMovimentacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
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

        List<CarrinhoItem> itensCarrinho = carrinho.getItens();

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

            if (produto != null && !produto.getAtivo()) {
                throw new IllegalArgumentException(String.format("O produto '%s' não está mais disponível para venda.", produto.getNome()));
            }

            if (!variante.getAtivo()) {
                throw new IllegalArgumentException(String.format("A variação '%s' do produto '%s' não está mais disponível.", variante.getNome(), produto != null ? produto.getNome() : ""));
            }

            if (variante.getStock() < quantidadeSolicitada) {
                throw new IllegalArgumentException(String.format("Estoque insuficiente para o produto: %s (SKU: %s). Disponível: %d", variante.getNome(), variante.getSku(), variante.getStock()));
            }

            Integer saldoAnterior = variante.getStock();
            Integer saldoAtual = saldoAnterior - quantidadeSolicitada;
            variante.setStock(saldoAtual);
            produtoVarianteRepository.save(variante);

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

            String urlImagemPrincipal = null;

            if (variante.getMidias() != null && !variante.getMidias().isEmpty()) {
                urlImagemPrincipal = variante.getMidias().stream()
                        .filter(m -> m.getTipo() == TipoMidia.IMAGE)
                        .sorted(Comparator.comparing(ProdutoMidia::getOrdem))
                        .map(ProdutoMidia::getPath)
                        .findFirst()
                        .orElse(null);
            }

            if (urlImagemPrincipal == null && produto != null && produto.getMidias() != null && !produto.getMidias().isEmpty()) {
                urlImagemPrincipal = produto.getMidias().stream()
                        .filter(m -> m.getTipo() == TipoMidia.IMAGE)
                        .sorted(Comparator.comparing(ProdutoMidia::getOrdem))
                        .map(ProdutoMidia::getPath)
                        .findFirst()
                        .orElse(null);
            }

            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setPedido(pedido);
            pedidoItem.setProduto(produto);
            pedidoItem.setVariante(variante);
            pedidoItem.setNomeProduto(variante.getNome());
            pedidoItem.setSku(variante.getSku());
            pedidoItem.setQuantidade(quantidadeSolicitada);
            pedidoItem.setPrecoUnitario(itemCarrinho.getPrecoUnitario());
            pedidoItem.setSubtotal(itemCarrinho.getPrecoUnitario().multiply(BigDecimal.valueOf(quantidadeSolicitada)));
            pedidoItem.setImagemUrl(urlImagemPrincipal);

            pedidoItemRepository.save(pedidoItem);
        }

        // A exclusão do carrinho foi removida daqui!
        return pedido;
    }
}