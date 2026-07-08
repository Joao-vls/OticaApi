package br.com.otica.otica_loja.UseCases.marcas;

import br.com.otica.otica_loja.Entity.Catalogo.Marca;
import br.com.otica.otica_loja.Repository.Catalogo.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ExcluirMarcaUseCase {

    @Autowired
    private MarcaRepository marcaRepository;

    /**
     * Realiza o Soft Delete da marca (inativa e define a data de exclusão).
     */
    @Transactional
    public void excluir(UUID marcaId) {
        Marca marca = marcaRepository.findById(marcaId)
                .orElseThrow(() -> new IllegalArgumentException("Marca não encontrada."));

        marca.setAtivo(false);
        marca.setDeletadoEm(OffsetDateTime.now());
        marca.setAtualizadoEm(OffsetDateTime.now());

        marcaRepository.save(marca);
    }

    /**
     * Realiza o Hard Delete (remove o registro definitivamente da tabela).
     */
    @Transactional
    public void excluirDefinitivo(UUID marcaId) {
        if (!marcaRepository.existsById(marcaId)) {
            throw new IllegalArgumentException("Marca não encontrada.");
        }
        marcaRepository.deleteById(marcaId);
    }
}