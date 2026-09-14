package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository; // 👈 Import necessário
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ValidarCupomUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    @Autowired
    private ProdutoVarianteRepository produtoVarianteRepository; // 👈 Injetado para buscar variantes se necessário

    /**
     * Sobrecarga para validação simples (ex: Painel Admin)
     */
    public Cupom validar(String codigo, BigDecimal valorPedido) {
        return validar(codigo, valorPedido, null, null);
    }

    /**
     * Valida um cupom pelo código, valor do pedido, usuário e produtos no carrinho.
     */
    public Cupom validar(String codigo, BigDecimal valorPedido, UUID usuarioId, List<UUID> produtosIds) {
        Cupom cupom = cupomRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado ou código inválido."));

        if (!cupom.getAtivo()) {
            throw new IllegalArgumentException("Este cupom está inativo ou expirado.");
        }

        OffsetDateTime agora = OffsetDateTime.now();
        if (cupom.getDataInicio() != null && agora.isBefore(cupom.getDataInicio())) {
            throw new IllegalArgumentException("Este cupom ainda não é válido.");
        }
        if (cupom.getDataFim() != null && agora.isAfter(cupom.getDataFim())) {
            throw new IllegalArgumentException("Este cupom já expirou.");
        }

        if (cupom.getQuantidadeTotal() != null && cupom.getQuantidadeUtilizada() >= cupom.getQuantidadeTotal()) {
            throw new IllegalArgumentException("O limite de usos deste cupom já foi atingido.");
        }

        if (cupom.getValorMinimoPedido() != null && valorPedido.compareTo(cupom.getValorMinimoPedido()) < 0) {
            throw new IllegalArgumentException("O valor mínimo para usar este cupom é R$ " + cupom.getValorMinimoPedido());
        }

        // Validação de Usuários Específicos (Ignora se usuarioId for null, ex: no admin)
        if (usuarioId != null && cupom.getUsuariosIdsEspecificos() != null && !cupom.getUsuariosIdsEspecificos().isEmpty()) {
            if (!cupom.getUsuariosIdsEspecificos().contains(usuarioId)) {
                throw new IllegalArgumentException("Este cupom não está disponível para o seu usuário.");
            }
        }

        // Validação de Produtos Específicos (Suporta ID de Produto Pai ou de Variante)
        if (produtosIds != null && !produtosIds.isEmpty() && cupom.getProdutosIdsEspecificos() != null && !cupom.getProdutosIdsEspecificos().isEmpty()) {
            boolean temProdutoValido = produtosIds.stream()
                    .anyMatch(idCarrinho -> {
                        // 1. Verifica se o ID bate diretamente com o produto restrito
                        if (cupom.getProdutosIdsEspecificos().contains(idCarrinho)) {
                            return true;
                        }

                        // 2. Se não bateu direto, checa se é o ID de uma variante cujo produto pai está no cupom
                        return produtoVarianteRepository.findById(idCarrinho)
                                .map(variante -> variante.getProduto() != null &&
                                        cupom.getProdutosIdsEspecificos().contains(variante.getProduto().getId()))
                                .orElse(false);
                    });

            if (!temProdutoValido) {
                throw new IllegalArgumentException("Este cupom não é válido para os produtos no seu carrinho.");
            }
        }

        return cupom;
    }
}