package br.com.otica.otica_loja.UseCases.marcas;

import br.com.otica.otica_loja.Repository.Catalogo.MarcaRepository;
import br.com.otica.otica_loja.dto.MarcaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarMarcasUseCase {

    @Autowired
    private MarcaRepository marcaRepository;

    /**
     * Retorna absolutamente tudo da tabela de marcas (incluindo deletadas).
     */
    @Transactional(readOnly = true)
    public List<MarcaResponseDTO> listarTodas() {
        return marcaRepository.findAll()
                .stream()
                .map(MarcaResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Retorna apenas as marcas onde o campo 'deletadoEm' está nulo.
     * Ideal para a listagem principal do Admin.
     */
    @Transactional(readOnly = true)
    public List<MarcaResponseDTO> listarNaoDeletadas() {
        return marcaRepository.findByDeletadoEmIsNull()
                .stream()
                .map(MarcaResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Retorna apenas as marcas não deletadas que estão ativas no e-commerce.
     */
    @Transactional(readOnly = true)
    public List<MarcaResponseDTO> listarAtivas() {
        return marcaRepository.findByAtivoTrueAndDeletadoEmIsNull()
                .stream()
                .map(MarcaResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Retorna as marcas que estão salvas, não foram excluídas, mas estão pausadas/inativas.
     */
    @Transactional(readOnly = true)
    public List<MarcaResponseDTO> listarInativas() {
        return marcaRepository.findByAtivoFalseAndDeletadoEmIsNull()
                .stream()
                .map(MarcaResponseDTO::fromEntity)
                .toList();
    }
}