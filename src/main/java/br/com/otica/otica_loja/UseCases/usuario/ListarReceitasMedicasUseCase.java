package br.com.otica.otica_loja.UseCases.receitaMedica;

import br.com.otica.otica_loja.Entity.ReceitaMedica.ReceitaMedica;
import br.com.otica.otica_loja.Repository.ReceitaMedica.ReceitaMedicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ListarReceitasMedicasUseCase {

    @Autowired
    private ReceitaMedicaRepository receitaMedicaRepository;

    /**
     * Lista todas as receitas médicas de um usuário.
     */
    public List<ReceitaMedica> listarPorUsuario(UUID usuarioId) {
        return receitaMedicaRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Lista receitas médicas filtrando pelo nome do arquivo.
     */
    public List<ReceitaMedica> listarPorNomeArquivo(String nomeArquivo) {
        return receitaMedicaRepository.findByNomeArquivoContainingIgnoreCase(nomeArquivo);
    }

    /**
     * Lista receitas médicas criadas após uma data.
     */
    public List<ReceitaMedica> listarCriadasDepois(OffsetDateTime data) {
        return receitaMedicaRepository.findByCriadoEmAfter(data);
    }

    /**
     * Lista receitas médicas criadas antes de uma data.
     */
    public List<ReceitaMedica> listarCriadasAntes(OffsetDateTime data) {
        return receitaMedicaRepository.findByCriadoEmBefore(data);
    }
}
