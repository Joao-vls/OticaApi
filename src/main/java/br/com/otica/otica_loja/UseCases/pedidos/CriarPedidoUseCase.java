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
import java.math.RoundingMode;
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

        // 3. Calcular subtotal de todos os itens
        BigDecimal subtotal = itensCarrinho.stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Aplicar cupom (se houver) e calcular o desconto com base no subtotal elegível
        BigDecimal desconto = BigDecimal.ZERO;
        Cupom cupom = null;

        if (codigoCupom != null && !codigoCupom.isBlank()) {
            Cupom cupomEncontrado = cupomRepository.findByCodigo(codigoCupom)
                    .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado."));

            if (!cupomEncontrado.getAtivo()) {
                throw new IllegalArgumentException("Este cupom está inativo ou expirado.");
            }
            if (cupomEncontrado.getQuantidadeTotal() != null && cupomEncontrado.getQuantidadeUtilizada() >= cupomEncontrado.getQuantidadeTotal()) {
                throw new IllegalArgumentException("O limite de usos para este cupom já foi atingido.");
            }

            if (cupomEncontrado.getUsuariosIdsEspecificos() != null && !cupomEncontrado.getUsuariosIdsEspecificos().isEmpty()) {
                if (!cupomEncontrado.getUsuariosIdsEspecificos().contains(usuarioId)) {
                    throw new IllegalArgumentException("Este cupom não está disponível para o seu usuário.");
                }
            }

            // 👇 LÓGICA DE LIMITE DE ITENS POR PEDIDO
            BigDecimal subtotalElegivel = BigDecimal.ZERO;

            // Pega o limite do cupom, ou infinito se não houver limite configurado
            int limiteRestante = cupomEncontrado.getLimiteItensPorPedido() != null ? cupomEncontrado.getLimiteItensPorPedido() : Integer.MAX_VALUE;

            if (cupomEncontrado.getProdutosIdsEspecificos() != null && !cupomEncontrado.getProdutosIdsEspecificos().isEmpty()) {

                // Filtra os itens do carrinho que pertencem a este cupom
                List<CarrinhoItem> itensElegiveis = itensCarrinho.stream()
                        .filter(item -> {
                            UUID idProdutoPai = item.getVariante().getProduto() != null ? item.getVariante().getProduto().getId() : null;
                            UUID idVariante = item.getVariante().getId();

                            return cupomEncontrado.getProdutosIdsEspecificos().contains(idProdutoPai) ||
                                    cupomEncontrado.getProdutosIdsEspecificos().contains(idVariante);
                        })
                        // Ordena do mais caro pro mais barato (beneficia o cliente no desconto)
                        .sorted((a, b) -> b.getPrecoUnitario().compareTo(a.getPrecoUnitario()))
                        .toList();

                if (itensElegiveis.isEmpty()) {
                    throw new IllegalArgumentException("Este cupom não é válido para os produtos no seu carrinho.");
                }

                // Soma o valor APENAS dos produtos elegíveis RESPEITANDO O LIMITE
                for (CarrinhoItem item : itensElegiveis) {
                    if (limiteRestante <= 0) break;

                    int qtdParaDesconto = Math.min(item.getQuantidade(), limiteRestante);
                    subtotalElegivel = subtotalElegivel.add(item.getPrecoUnitario().multiply(BigDecimal.valueOf(qtdParaDesconto)));
                    limiteRestante -= qtdParaDesconto;
                }

            } else {
                // Se o cupom não tem produtos específicos, aplica o limite nos produtos gerais
                List<CarrinhoItem> todosOrdenados = itensCarrinho.stream()
                        .sorted((a, b) -> b.getPrecoUnitario().compareTo(a.getPrecoUnitario()))
                        .toList();

                for (CarrinhoItem item : todosOrdenados) {
                    if (limiteRestante <= 0) break;

                    int qtdParaDesconto = Math.min(item.getQuantidade(), limiteRestante);
                    subtotalElegivel = subtotalElegivel.add(item.getPrecoUnitario().multiply(BigDecimal.valueOf(qtdParaDesconto)));
                    limiteRestante -= qtdParaDesconto;
                }
            }

            // Calcula o Desconto USANDO O SUBTOTAL ELEGÍVEL DE FORMA SEGURA (Prevenindo ArithmeticException)
            desconto = switch (cupomEncontrado.getTipo().toLowerCase()) {
                case "percentual" -> subtotalElegivel.multiply(cupomEncontrado.getValor())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                case "fixo" -> cupomEncontrado.getValor().min(subtotalElegivel); // Desconto não pode ser maior que o valor dos itens elegíveis
                case "frete" -> valorFrete.min(cupomEncontrado.getValor());
                default -> BigDecimal.ZERO;
            };

            // Atualiza o uso do cupom
            cupomEncontrado.setQuantidadeUtilizada(cupomEncontrado.getQuantidadeUtilizada() + 1);
            if (Boolean.TRUE.equals(cupomEncontrado.getUsoUnico()) ||
                    (cupomEncontrado.getQuantidadeTotal() != null && cupomEncontrado.getQuantidadeUtilizada() >= cupomEncontrado.getQuantidadeTotal())) {
                cupomEncontrado.setAtivo(false);
            }

            cupom = cupomRepository.save(cupomEncontrado);
        }

        // 5. Calcular total final do pedido
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
            movimentacao.setObservacao("Reserva de estoque - Pedido aguardando pagamento.");
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

        return pedido;
    }
}