package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import br.com.otica.otica_loja.dto.CupomDisponivelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListarCuponsDisponiveisClienteUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    public List<CupomDisponivelResponse> listarParaCliente(UUID usuarioId) {
        OffsetDateTime agora = OffsetDateTime.now();

        // Busca os cupons que estão ativos, dentro do prazo e com saldo
        List<Cupom> cuponsValidos = cupomRepository.findValidCupons(agora, agora);

        return cuponsValidos.stream()
                // Regra: O cupom deve ser público (lista vazia) OU o usuário logado deve estar na lista de permitidos
                .filter(cupom -> cupom.getUsuariosIdsEspecificos() == null ||
                        cupom.getUsuariosIdsEspecificos().isEmpty() ||
                        cupom.getUsuariosIdsEspecificos().contains(usuarioId))
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());
    }

    private CupomDisponivelResponse mapearParaResponse(Cupom cupom) {
        // Calcula quantos cupons ainda restam (caso tenha limite)
        Integer quantidadeDisponivel = null;
        if (cupom.getQuantidadeTotal() != null) {
            quantidadeDisponivel = cupom.getQuantidadeTotal() - cupom.getQuantidadeUtilizada();
        }

        return new CupomDisponivelResponse(
                cupom.getCodigo(),
                cupom.getDescricao(),
                cupom.getTipo(),
                cupom.getValor(),
                cupom.getValorMinimoPedido(),
                quantidadeDisponivel,
                cupom.getProdutosIdsEspecificos(),
                cupom.getCategoriasIdsEspecificas()
        );
    }
}