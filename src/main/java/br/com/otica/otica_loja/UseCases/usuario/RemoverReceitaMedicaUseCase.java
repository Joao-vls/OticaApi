package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.ReceitaMedica.ReceitaMedica;
import br.com.otica.otica_loja.Repository.ReceitaMedica.ReceitaMedicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RemoverReceitaMedicaUseCase {

    @Autowired
    private ReceitaMedicaRepository receitaMedicaRepository;

    /**
     * Remove uma receita médica pelo ID.
     */
    public void remover(UUID receitaId) {
        // 1. Buscar receita médica
        Optional<ReceitaMedica> receitaOpt = receitaMedicaRepository.findById(receitaId);
        if (receitaOpt.isEmpty()) {
            throw new IllegalArgumentException("Receita médica não encontrada.");
        }

        ReceitaMedica receita = receitaOpt.get();

        // 2. Excluir receita médica
        receitaMedicaRepository.delete(receita);
    }
}
