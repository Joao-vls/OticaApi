package br.com.otica.otica_loja.UseCases.usuario;

import br.com.otica.otica_loja.Entity.ReceitaMedica.ReceitaMedica;
import br.com.otica.otica_loja.Repository.ReceitaMedica.ReceitaMedicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class UploadReceitaMedicaUseCase {

    @Autowired
    private ReceitaMedicaRepository receitaMedicaRepository;

    /**
     * Faz upload de uma receita médica e salva no banco.
     */
    public ReceitaMedica upload(UUID usuarioId,
                                String nomeArquivo,
                                String arquivoPath,
                                String observacoes) {

        // 1. Verificar se já existe receita com o mesmo arquivoPath
        if (receitaMedicaRepository.existsByArquivoPath(arquivoPath)) {
            throw new IllegalArgumentException("Já existe uma receita médica com este arquivo.");
        }

        // 2. Criar nova receita médica
        ReceitaMedica receita = new ReceitaMedica();
        receita.setUsuarioId(usuarioId);
        receita.setNomeArquivo(nomeArquivo);
        receita.setArquivoPath(arquivoPath);
        receita.setObservacoes(observacoes);
        receita.setCriadoEm(OffsetDateTime.now());

        // 3. Persistir no banco
        return receitaMedicaRepository.save(receita);
    }
}
