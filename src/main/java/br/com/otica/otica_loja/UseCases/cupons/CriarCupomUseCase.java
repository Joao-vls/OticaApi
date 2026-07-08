package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class CriarCupomUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    /**
     * Cria um novo cupom de desconto.
     */
    public Cupom criar(String codigo,
                       String descricao,
                       String tipo,
                       BigDecimal valor,
                       BigDecimal valorMinimoPedido,
                       Integer quantidadeTotal,
                       OffsetDateTime dataInicio,
                       OffsetDateTime dataFim) {

        // 1. Verificar se já existe cupom com o mesmo código
        if (cupomRepository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException("Já existe um cupom com este código.");
        }

        // 2. Criar novo cupom
        Cupom cupom = new Cupom();
        cupom.setCodigo(codigo);
        cupom.setDescricao(descricao);
        cupom.setTipo(tipo.toLowerCase()); // normalizar tipo
        cupom.setValor(valor);
        cupom.setValorMinimoPedido(valorMinimoPedido);
        cupom.setQuantidadeTotal(quantidadeTotal);
        cupom.setQuantidadeUtilizada(0);
        cupom.setDataInicio(dataInicio);
        cupom.setDataFim(dataFim);
        cupom.setAtivo(true);
        cupom.setCriadoEm(OffsetDateTime.now());
        cupom.setAtualizadoEm(OffsetDateTime.now());

        // 3. Persistir no banco
        return cupomRepository.save(cupom);
    }
}
