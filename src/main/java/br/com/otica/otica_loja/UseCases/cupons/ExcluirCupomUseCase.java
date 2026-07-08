package br.com.otica.otica_loja.UseCases.cupons;

import br.com.otica.otica_loja.Repository.Comercial.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExcluirCupomUseCase {

    @Autowired
    private CupomRepository cupomRepository;

    /**
     * Exclui um cupom pelo ID.
     */
    public void excluir(UUID cupomId) {
        // 1. Verificar se o cupom existe
        if (!cupomRepository.existsById(cupomId)) {
            throw new IllegalArgumentException("Cupom não encontrado.");
        }

        // 2. Excluir cupom
        cupomRepository.deleteById(cupomId);
    }
}
