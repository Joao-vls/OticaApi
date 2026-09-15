package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class AtualizarCupomUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    /**
     * Atualiza os dados de um cupom existente.
     */
    public Cupom atualizar(UUID cupomId,
                           String codigo,
                           String descricao,
                           String tipo,
                           BigDecimal valor,
                           BigDecimal valorMinimoPedido,
                           Integer quantidadeTotal,
                           Integer limiteItensPorPedido,
                           OffsetDateTime dataInicio,
                           OffsetDateTime dataFim,
                           Boolean ativo,
                           List<UUID> usuariosIdsEspecificos, // 👈 Novo
                           List<UUID> produtosIdsEspecificos, // 👈 Novo
                           Boolean usoUnico) {                // 👈 Novo

        // 1. Buscar cupom existente
        Cupom cupom = cupomRepository.findById(cupomId)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado."));

        // 2. Verificar se o novo código já existe em outro cupom
        if (codigo != null && !codigo.equals(cupom.getCodigo()) && cupomRepository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException("Já existe outro cupom com este código.");
        }

        // 3. Atualizar campos básicos
        if (codigo != null) cupom.setCodigo(codigo);
        if (descricao != null) cupom.setDescricao(descricao);
        if (tipo != null) cupom.setTipo(tipo.toLowerCase());
        if (valor != null) cupom.setValor(valor);
        if (valorMinimoPedido != null) cupom.setValorMinimoPedido(valorMinimoPedido);
        if (quantidadeTotal != null) cupom.setQuantidadeTotal(quantidadeTotal);
        cupom.setLimiteItensPorPedido(limiteItensPorPedido);
        if (dataInicio != null) cupom.setDataInicio(dataInicio);
        if (dataFim != null) cupom.setDataFim(dataFim);
        if (ativo != null) cupom.setAtivo(ativo);
        if (usoUnico != null) cupom.setUsoUnico(usoUnico);

        // 4. Atualizar as listas (Se vier nulo, ignora. Se vier lista vazia, limpa as restrições)
        if (usuariosIdsEspecificos != null) {
            cupom.setUsuariosIdsEspecificos(new HashSet<>(usuariosIdsEspecificos));
        }

        if (produtosIdsEspecificos != null) {
            cupom.setProdutosIdsEspecificos(new HashSet<>(produtosIdsEspecificos));
        }

        cupom.setAtualizadoEm(OffsetDateTime.now());

        // 5. Persistir alterações
        return cupomRepository.save(cupom);
    }
}