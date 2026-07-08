package br.com.otica.otica_loja.UseCases.marcas;

import br.com.otica.otica_loja.Entity.Catalogo.Marca;
import br.com.otica.otica_loja.Repository.Catalogo.MarcaRepository;
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
    public List<Marca> listarTodas() {
        return marcaRepository.findAll();
    }

    /**
     * Retorna apenas as marcas onde o campo 'deletadoEm' está nulo.
     * Ideal para a listagem principal do Admin.
     */
    @Transactional(readOnly = true)
    public List<Marca> listarNaoDeletadas() {
        return marcaRepository.findByDeletadoEmIsNull();
    }

    /**
     * Retorna apenas as marcas não deletadas que estão ativas no e-commerce.
     */
    @Transactional(readOnly = true)
    public List<Marca> listarAtivas() {
        return marcaRepository.findByAtivoTrueAndDeletadoEmIsNull();
    }

    /**
     * Retorna as marcas que estão salvas, não foram excluídas, mas estão pausadas/inativas.
     */
    @Transactional(readOnly = true)
    public List<Marca> listarInativas() {
        return marcaRepository.findByAtivoFalseAndDeletadoEmIsNull();
    }
}