package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CriarCupomUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    public Cupom criar(String codigo, String descricao, String tipo, BigDecimal valor,
                       BigDecimal valorMinimoPedido, Integer quantidadeTotal,
                       OffsetDateTime dataInicio, OffsetDateTime dataFim,
                       UUID usuarioIdEspecifico, UUID produtoIdEspecifico, Boolean usoUnico) { // Novos parâmetros

        if (cupomRepository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException("Já existe um cupom com este código.");
        }

        Cupom cupom = new Cupom();
        cupom.setCodigo(codigo);
        cupom.setDescricao(descricao);
        cupom.setTipo(tipo.toLowerCase());
        cupom.setValor(valor);
        cupom.setValorMinimoPedido(valorMinimoPedido);

        cupom.setQuantidadeTotal(quantidadeTotal);
        cupom.setQuantidadeUtilizada(0);

        // Atribuindo os novos campos
        cupom.setUsuarioIdEspecifico(usuarioIdEspecifico);
        cupom.setProdutoIdEspecifico(produtoIdEspecifico);
        cupom.setUsoUnico(usoUnico != null ? usoUnico : false);

        cupom.setDataInicio(dataInicio);
        cupom.setDataFim(dataFim);
        cupom.setAtivo(true);
        cupom.setCriadoEm(OffsetDateTime.now());
        cupom.setAtualizadoEm(OffsetDateTime.now());

        return cupomRepository.save(cupom);
    }
}