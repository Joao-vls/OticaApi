package br.com.otica.otica_loja.UseCases.pedidos;

import br.com.otica.otica_loja.Entity.Auth.Perfil;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Entity.Estoque.EstoqueMovimentacao;
import br.com.otica.otica_loja.Entity.Pedidos.Pagamento;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.PedidoItem;
import br.com.otica.otica_loja.Entity.Pedidos.PedidoStatusHistorico;
import br.com.otica.otica_loja.Repository.Auth.PerfilRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import br.com.otica.otica_loja.Repository.Estoque.EstoqueMovimentacaoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PagamentoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoStatusHistoricoRepository;
import br.com.otica.otica_loja.dto.CriarPedidoFisicoRequest;
import br.com.otica.otica_loja.dto.ItemPedidoFisicoDTO;
import br.com.otica.otica_loja.enums.MetodoPagamento;
import br.com.otica.otica_loja.enums.StatusPagamento;
import br.com.otica.otica_loja.enums.StatusPedido;
import br.com.otica.otica_loja.enums.TipoMidia;
import br.com.otica.otica_loja.enums.TipoMovimentacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CriarPedidoFisicoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final PagamentoRepository pagamentoRepository;
    private final PedidoStatusHistoricoRepository historicoRepository;
    private final ProdutoVarianteRepository produtoVarianteRepository;
    private final EstoqueMovimentacaoRepository estoqueMovimentacaoRepository;
    private final CupomRepository cupomRepository;
    private final PerfilRepository perfilRepository;
    @Transactional
    public Pedido executar(CriarPedidoFisicoRequest request, UUID atendenteId) {

        // 1. Validação antecipada de estoque (Fail-Fast)
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemPedidoFisicoDTO itemDto : request.itens()) {
            ProdutoVariante variante = produtoVarianteRepository.findById(itemDto.varianteId())
                    .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada ID: " + itemDto.varianteId()));

            BigDecimal precoItem = getPrecoItem(itemDto, variante);
            subtotal = subtotal.add(precoItem.multiply(BigDecimal.valueOf(itemDto.quantidade())));
        }

        // 2. Cálculo de descontos (Cupom + Desconto Manual do Balcão)
        BigDecimal descontoTotal = BigDecimal.ZERO;
        Cupom cupomAplicado = null;

        if (request.codigoCupom() != null && !request.codigoCupom().isBlank()) {
            cupomAplicado = cupomRepository.findByCodigoIgnoreCase(request.codigoCupom())
                    .orElseThrow(() -> new IllegalArgumentException("Cupom inválido: " + request.codigoCupom()));

            if (!Boolean.TRUE.equals(cupomAplicado.getAtivo())) {
                throw new IllegalArgumentException("O cupom informado está inativo.");
            }

            BigDecimal descontoCupom = switch (cupomAplicado.getTipo().toLowerCase()) {
                case "percentual" -> subtotal.multiply(cupomAplicado.getValor())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                case "fixo" -> cupomAplicado.getValor().min(subtotal);
                default -> BigDecimal.ZERO;
            };

            descontoTotal = descontoTotal.add(descontoCupom);
            cupomAplicado.setQuantidadeUtilizada(cupomAplicado.getQuantidadeUtilizada() + 1);
            cupomRepository.save(cupomAplicado);
        }

        if (request.descontoManual() != null && request.descontoManual().compareTo(BigDecimal.ZERO) > 0) {
            descontoTotal = descontoTotal.add(request.descontoManual());
        }

        BigDecimal total = subtotal.subtract(descontoTotal);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }


        // 3. Status inicial (Pronta Entrega -> ENTREGUE | Montagem/Laboratório -> SEPARACAO)
        boolean prontaEntrega = Boolean.TRUE.equals(request.prontaEntrega());
        StatusPedido statusInicial = prontaEntrega ? StatusPedido.ENTREGUE : StatusPedido.SEPARACAO;

        // 👇 NOVA LÓGICA DE IDENTIFICAÇÃO DO CLIENTE
        UUID idDoClienteNoBanco = request.clienteId();

        // Se não mandou um ID direto, mas mandou um CPF, vamos verificar se esse CPF já tem cadastro na loja!
        if (idDoClienteNoBanco == null && request.clienteCpf() != null && !request.clienteCpf().isBlank()) {
            Optional<Perfil> perfilExistente = perfilRepository.findByCpf(request.clienteCpf().replaceAll("\\D", "")); // Limpa pontuação
            if (perfilExistente.isPresent()) {
                idDoClienteNoBanco = perfilExistente.get().getId();
            }
        }
        // 4. Criação do Pedido
        Pedido pedido = new Pedido();

        // Se achou um ID (ou enviou na request), vincula ao usuário. Senão, fica null (ou vinculado ao atendente, caso seja a sua regra de negócio, mas recomendo deixar NULL se for cliente avulso).
        pedido.setUsuarioId(idDoClienteNoBanco);

        // Salva os dados avulsos para o recibo/histórico, mesmo se vinculou ao ID (útil para clientes não cadastrados)
        pedido.setClienteNome(request.clienteNome());
        // Salva CPF limpo (só números)
        pedido.setClienteCpf(request.clienteCpf() != null ? request.clienteCpf().replaceAll("\\D", "") : null);
        pedido.setClienteTelefone(request.clienteTelefone());
        pedido.setClienteEmail(request.clienteEmail());

        pedido.setCupom(cupomAplicado);
        pedido.setSubtotal(subtotal);
        pedido.setDesconto(descontoTotal);
        pedido.setFrete(BigDecimal.ZERO); // Venda em loja física não tem frete
        pedido.setTotal(total);
        pedido.setStatus(statusInicial);

        StringBuilder obs = new StringBuilder();
        obs.append("Venda Presencial (Balcão) | Atendente ID: ").append(atendenteId);
        if (request.metodoPagamento() == MetodoPagamento.DINHEIRO && request.valorRecebido() != null) {
            BigDecimal troco = request.valorRecebido().subtract(total);
            obs.append(" | Recebido: R$ ").append(request.valorRecebido())
                    .append(" | Troco: R$ ").append(troco.max(BigDecimal.ZERO));
        }
        if (request.observacoes() != null && !request.observacoes().isBlank()) {
            obs.append(" | Obs: ").append(request.observacoes());
        }
        pedido.setObservacoes(obs.toString());
        pedido.setCriadoEm(OffsetDateTime.now());
        pedido.setAtualizadoEm(OffsetDateTime.now());

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 5. Baixa no estoque e criação dos itens
        for (ItemPedidoFisicoDTO itemDto : request.itens()) {

            // CORREÇÃO 2: Substituindo .get() por .orElseThrow()
            ProdutoVariante variante = produtoVarianteRepository.findById(itemDto.varianteId())
                    .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada: " + itemDto.varianteId()));

            Produto produto = variante.getProduto();

            // Atualiza Saldo do Estoque
            int saldoAnterior = variante.getStock();
            int saldoAtual = saldoAnterior - itemDto.quantidade();
            variante.setStock(saldoAtual);
            produtoVarianteRepository.save(variante);

            // Registro de movimentação de saída
            EstoqueMovimentacao movimentacao = new EstoqueMovimentacao();
            movimentacao.setVariante(variante);
            movimentacao.setTipo(TipoMovimentacao.SAIDA);
            movimentacao.setQuantidade(itemDto.quantidade());
            movimentacao.setSaldoAnterior(saldoAnterior);
            movimentacao.setSaldoAtual(saldoAtual);
            movimentacao.setUsuarioId(atendenteId);
            movimentacao.setObservacao("Venda Balcão / Loja Física - Pedido #" + pedidoSalvo.getId());
            movimentacao.setCriadoEm(OffsetDateTime.now());
            estoqueMovimentacaoRepository.save(movimentacao);

            // Resolução da Imagem Principal
            String urlImagemPrincipal = null;
            if (variante.getMidias() != null && !variante.getMidias().isEmpty()) {
                urlImagemPrincipal = variante.getMidias().stream()
                        .filter(m -> m.getTipo() == TipoMidia.IMAGE)
                        .sorted(Comparator.comparing(ProdutoMidia::getOrdem))
                        .map(ProdutoMidia::getPath)
                        .findFirst().orElse(null);
            }
            if (urlImagemPrincipal == null && produto != null && produto.getMidias() != null && !produto.getMidias().isEmpty()) {
                urlImagemPrincipal = produto.getMidias().stream()
                        .filter(m -> m.getTipo() == TipoMidia.IMAGE)
                        .sorted(Comparator.comparing(ProdutoMidia::getOrdem))
                        .map(ProdutoMidia::getPath)
                        .findFirst().orElse(null);
            }

            // Reutilizando a mesma lógica do Preço do Passo 1
            BigDecimal precoPadrao; // NOTA: Se na sua classe Produto o nome for 'getPrecoBase()', altere aqui.
            if (variante.getPriceOverride() != null) {
                precoPadrao = variante.getPriceOverride();
            } else {
                assert produto != null;
                precoPadrao = produto.getPreco();
            }

            BigDecimal precoUnitario = itemDto.precoUnitario() != null ? itemDto.precoUnitario() : precoPadrao;

            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setPedido(pedidoSalvo);
            pedidoItem.setProduto(produto);
            pedidoItem.setVariante(variante);
            pedidoItem.setNomeProduto(variante.getNome());
            pedidoItem.setSku(variante.getSku());
            pedidoItem.setQuantidade(itemDto.quantidade());
            pedidoItem.setPrecoUnitario(precoUnitario);
            pedidoItem.setSubtotal(precoUnitario.multiply(BigDecimal.valueOf(itemDto.quantidade())));
            pedidoItem.setImagemUrl(urlImagemPrincipal);

            pedidoItemRepository.save(pedidoItem);
        }

        // 6. Registro do Pagamento Concluído na tabela 'pagamentos'
        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedidoSalvo);
        pagamento.setGateway("BALCAO_PRESENCIAL");
        pagamento.setGatewayTransactionId("POS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pagamento.setMetodo(request.metodoPagamento());
        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamento.setValor(total);
        pagamento.setPayload("{}");
        pagamento.setCriadoEm(OffsetDateTime.now());
        pagamento.setAtualizadoEm(OffsetDateTime.now());
        pagamentoRepository.save(pagamento);

        // 7. Registro do Histórico de Status
        PedidoStatusHistorico historico = new PedidoStatusHistorico();
        historico.setPedido(pedidoSalvo);
        historico.setStatus(statusInicial.name());
        historico.setObservacao("Venda presencial realizada no balcão via " + request.metodoPagamento());
        historico.setCriadoEm(OffsetDateTime.now());
        historicoRepository.save(historico);

        return pedidoSalvo;
    }

    private static BigDecimal getPrecoItem(ItemPedidoFisicoDTO itemDto, ProdutoVariante variante) {
        Produto produto = variante.getProduto();

        if (produto != null && Boolean.FALSE.equals(produto.getAtivo())) {
            throw new IllegalArgumentException("O produto '" + produto.getNome() + "' não está ativo para venda.");
        }

        if (Boolean.FALSE.equals(variante.getAtivo())) {
            throw new IllegalArgumentException("A variação '" + variante.getNome() + "' está inativa.");
        }

        if (variante.getStock() < itemDto.quantidade()) {
            throw new IllegalArgumentException(String.format(
                    "Estoque insuficiente para '%s' (SKU: %s). Disponível: %d, Solicitado: %d",
                    variante.getNome(), variante.getSku(), variante.getStock(), itemDto.quantidade()
            ));
        }

        // CORREÇÃO 1: Resolve preço verificando o priceOverride da Variante ou o preço base do Produto
        BigDecimal precoPadrao; // NOTA: Se na sua classe Produto o nome for 'getPrecoBase()', altere aqui.
        if (variante.getPriceOverride() != null) {
            precoPadrao = variante.getPriceOverride();
        } else {
            assert produto != null;
            precoPadrao = produto.getPreco();
        }

        BigDecimal precoItem = itemDto.precoUnitario() != null ? itemDto.precoUnitario() : precoPadrao;
        return precoItem;
    }
}