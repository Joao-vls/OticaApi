package br.com.otica.otica_loja.UseCases.pedidos;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Auth.Perfil;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
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
import br.com.otica.otica_loja.Repository.Auth.PerfilRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import br.com.otica.otica_loja.Repository.Estoque.EstoqueMovimentacaoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.TipoMidia;
import br.com.otica.otica_loja.enums.TipoMovimentacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CriarPedidoUseCase {

    private final CarrinhoRepository carrinhoRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final EnderecoRepository enderecoRepository;
    private final CupomRepository cupomRepository;
    private final ProdutoVarianteRepository produtoVarianteRepository;
    private final EstoqueMovimentacaoRepository estoqueMovimentacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;

    @Transactional
    public Pedido criar(UUID usuarioId, BigDecimal valorFrete, String observacoes) {
        // 1. Busca e Valida Carrinho
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado."));
        List<CarrinhoItem> itensCarrinho = carrinho.getItens();
        if (itensCarrinho.isEmpty()) throw new IllegalArgumentException("Carrinho está vazio.");

        // 2. Validação Antecipada de Estoque (Fail-Fast)
        for (CarrinhoItem itemCarrinho : itensCarrinho) {
            ProdutoVariante variante = itemCarrinho.getVariante();
            Produto produto = variante.getProduto();
            Integer quantidadeSolicitada = itemCarrinho.getQuantidade();
            if (produto != null && !Boolean.TRUE.equals(produto.getAtivo())) {
                throw new IllegalArgumentException(String.format("O produto '%s' não está mais disponível para venda.", produto.getNome()));
            }
            if (!Boolean.TRUE.equals(variante.getAtivo())) {
                throw new IllegalArgumentException(String.format("A variação '%s' não está mais disponível.", variante.getNome()));
            }
            if (variante.getStock() < quantidadeSolicitada) {
                throw new IllegalArgumentException(String.format("Estoque insuficiente para o produto: %s (SKU: %s). Disponível: %d", variante.getNome(), variante.getSku(), variante.getStock()));
            }
        }

        // 3. Endereço
        Endereco endereco = enderecoRepository.findByUsuarioIdAndIsDefaultTrue(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Endereço padrão não encontrado."));

        // 4. Subtotal
        BigDecimal subtotal = itensCarrinho.stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Cupom
        BigDecimal desconto = BigDecimal.ZERO;
        Cupom cupom = carrinho.getCupom();

        if (cupom != null) {
            if (!Boolean.TRUE.equals(cupom.getAtivo())) {
                throw new IllegalArgumentException("O cupom aplicado no carrinho está inativo ou expirado.");
            }
            if (cupom.getQuantidadeTotal() != null && cupom.getQuantidadeUtilizada() >= cupom.getQuantidadeTotal()) {
                throw new IllegalArgumentException("O limite de usos para o cupom aplicado já foi atingido.");
            }
            if (cupom.getUsuariosIdsEspecificos() != null && !cupom.getUsuariosIdsEspecificos().isEmpty()) {
                if (!cupom.getUsuariosIdsEspecificos().contains(usuarioId)) {
                    throw new IllegalArgumentException("Este cupom não está disponível para o seu usuário.");
                }
            }

            BigDecimal subtotalElegivel = BigDecimal.ZERO;
            int limiteRestante = cupom.getLimiteItensPorPedido() != null ? cupom.getLimiteItensPorPedido() : Integer.MAX_VALUE;

            if (cupom.getProdutosIdsEspecificos() != null && !cupom.getProdutosIdsEspecificos().isEmpty()) {
                final var produtosIdsEspecificos = cupom.getProdutosIdsEspecificos();
                List<CarrinhoItem> itensElegiveis = itensCarrinho.stream()
                        .filter(item -> {
                            UUID idProdutoPai = item.getVariante().getProduto() != null ? item.getVariante().getProduto().getId() : null;
                            UUID idVariante = item.getVariante().getId();
                            return produtosIdsEspecificos.contains(idProdutoPai) || produtosIdsEspecificos.contains(idVariante);
                        })
                        .sorted((a, b) -> b.getPrecoUnitario().compareTo(a.getPrecoUnitario()))
                        .toList();

                if (itensElegiveis.isEmpty()) throw new IllegalArgumentException("O cupom no carrinho não é válido para os produtos atuais.");

                for (CarrinhoItem item : itensElegiveis) {
                    if (limiteRestante <= 0) break;
                    int qtdParaDesconto = Math.min(item.getQuantidade(), limiteRestante);
                    subtotalElegivel = subtotalElegivel.add(item.getPrecoUnitario().multiply(BigDecimal.valueOf(qtdParaDesconto)));
                    limiteRestante -= qtdParaDesconto;
                }
            } else {
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

            desconto = switch (cupom.getTipo().toLowerCase()) {
                case "percentual" -> subtotalElegivel.multiply(cupom.getValor()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                case "fixo" -> cupom.getValor().min(subtotalElegivel);
                case "frete" -> valorFrete.min(cupom.getValor());
                default -> BigDecimal.ZERO;
            };

            cupom.setQuantidadeUtilizada(cupom.getQuantidadeUtilizada() + 1);
            if (Boolean.TRUE.equals(cupom.getUsoUnico()) || (cupom.getQuantidadeTotal() != null && cupom.getQuantidadeUtilizada() >= cupom.getQuantidadeTotal())) {
                cupom.setAtivo(false);
            }
            cupom = cupomRepository.save(cupom);
        }

        // 6. Total
        BigDecimal total = subtotal.subtract(desconto).add(valorFrete);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        // 7. Resgate limpo dos dados do cliente (evita conflitos de variáveis em lambda)
        String clienteEmail = usuarioRepository.findById(usuarioId)
                .map(Usuario::getEmail)
                .orElse(null);

        String clienteNome = null;
        String clienteTelefone = null;
        String clienteCpf = null;

        try {
            Optional<Perfil> perfilOpt = perfilRepository.findById(usuarioId);
            if (perfilOpt.isPresent()) {
                Perfil perfil = perfilOpt.get();
                clienteNome = perfil.getNome();
                clienteTelefone = perfil.getTelefone();
                if (perfil.getCpf() != null) {
                    clienteCpf = perfil.getCpf().replaceAll("\\D", "");
                }
            }
        } catch (Exception e) {
            // Ignora falhas caso o perfil não exista perfeitamente
        }

        // Fallback robusto para o endereço se o perfil estiver incompleto
        if (clienteNome == null || clienteNome.isBlank()) {
            clienteNome = endereco.getNomeRecebedor();
        }
        if (clienteTelefone == null || clienteTelefone.isBlank()) {
            clienteTelefone = endereco.getTelefoneRecebedor();
        }

        // 8. Instanciar Pedido já com todos os dados preenchidos
        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuarioId);
        pedido.setEnderecoId(endereco.getId());
        pedido.setClienteEmail(clienteEmail);
        pedido.setClienteNome(clienteNome);
        pedido.setClienteTelefone(clienteTelefone);
        pedido.setClienteCpf(clienteCpf);
        pedido.setCupom(cupom);
        pedido.setSubtotal(subtotal);
        pedido.setDesconto(desconto);
        pedido.setFrete(valorFrete);
        pedido.setTotal(total);
        pedido.setObservacoes(observacoes);
        pedido.setCriadoEm(OffsetDateTime.now());
        pedido.setAtualizadoEm(OffsetDateTime.now());

        pedido = pedidoRepository.save(pedido);

        // 9. Salvar Itens e Baixar Estoque
        for (CarrinhoItem itemCarrinho : itensCarrinho) {
            ProdutoVariante variante = itemCarrinho.getVariante();
            Produto produto = variante.getProduto();
            Integer quantidadeSolicitada = itemCarrinho.getQuantidade();
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
                        .map(ProdutoMidia::getPath).findFirst().orElse(null);
            }
            if (urlImagemPrincipal == null && produto != null && produto.getMidias() != null && !produto.getMidias().isEmpty()) {
                urlImagemPrincipal = produto.getMidias().stream()
                        .filter(m -> m.getTipo() == TipoMidia.IMAGE)
                        .sorted(Comparator.comparing(ProdutoMidia::getOrdem))
                        .map(ProdutoMidia::getPath).findFirst().orElse(null);
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