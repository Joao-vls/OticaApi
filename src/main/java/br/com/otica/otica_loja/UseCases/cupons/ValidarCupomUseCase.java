package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ValidarCupomUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    @Autowired
    private ProdutoVarianteRepository produtoVarianteRepository;

    public Cupom validar(String codigo, BigDecimal valorPedido) {
        return validar(codigo, valorPedido, null, null);
    }

    public Cupom validar(String codigo, BigDecimal valorPedido, UUID usuarioId, List<UUID> produtosIds) {
        Cupom cupom = cupomRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado ou código inválido."));

        if (!cupom.getAtivo()) throw new IllegalArgumentException("Este cupom está inativo ou expirado.");

        OffsetDateTime agora = OffsetDateTime.now();
        if (cupom.getDataInicio() != null && agora.isBefore(cupom.getDataInicio())) throw new IllegalArgumentException("Este cupom ainda não é válido.");
        if (cupom.getDataFim() != null && agora.isAfter(cupom.getDataFim())) throw new IllegalArgumentException("Este cupom já expirou.");
        if (cupom.getQuantidadeTotal() != null && cupom.getQuantidadeUtilizada() >= cupom.getQuantidadeTotal()) throw new IllegalArgumentException("O limite de usos deste cupom já foi atingido.");
        if (cupom.getValorMinimoPedido() != null && valorPedido.compareTo(cupom.getValorMinimoPedido()) < 0) throw new IllegalArgumentException("O valor mínimo para usar este cupom é R$ " + cupom.getValorMinimoPedido());

        if (usuarioId != null && cupom.getUsuariosIdsEspecificos() != null && !cupom.getUsuariosIdsEspecificos().isEmpty()) {
            if (!cupom.getUsuariosIdsEspecificos().contains(usuarioId)) {
                throw new IllegalArgumentException("Este cupom não está disponível para o seu usuário.");
            }
        }

        if (produtosIds != null && !produtosIds.isEmpty() && cupom.getProdutosIdsEspecificos() != null && !cupom.getProdutosIdsEspecificos().isEmpty()) {
            boolean temProdutoValido = produtosIds.stream()
                    .anyMatch(idCarrinho -> {
                        if (cupom.getProdutosIdsEspecificos().contains(idCarrinho)) return true;
                        return produtoVarianteRepository.findById(idCarrinho)
                                .map(variante -> variante.getProduto() != null && cupom.getProdutosIdsEspecificos().contains(variante.getProduto().getId()))
                                .orElse(false);
                    });

            if (!temProdutoValido) throw new IllegalArgumentException("Este cupom não é válido para os produtos no seu carrinho.");
        }

        return cupom;
    }

    // 👇 NOVA FUNÇÃO: Resgata os IDs das Variantes que foram aprovadas para o Angular conseguir ler.
    public Set<UUID> obterVariantesElegiveis(Cupom cupom, List<UUID> produtosIdsCarrinho) {
        Set<UUID> elegiveis = new HashSet<>();
        if (cupom.getProdutosIdsEspecificos() == null || cupom.getProdutosIdsEspecificos().isEmpty() || produtosIdsCarrinho == null) {
            return elegiveis;
        }

        for (UUID idCarrinho : produtosIdsCarrinho) {
            if (cupom.getProdutosIdsEspecificos().contains(idCarrinho)) {
                elegiveis.add(idCarrinho);
            } else {
                produtoVarianteRepository.findById(idCarrinho).ifPresent(variante -> {
                    if (variante.getProduto() != null && cupom.getProdutosIdsEspecificos().contains(variante.getProduto().getId())) {
                        elegiveis.add(idCarrinho);
                    }
                });
            }
        }
        return elegiveis;
    }
}