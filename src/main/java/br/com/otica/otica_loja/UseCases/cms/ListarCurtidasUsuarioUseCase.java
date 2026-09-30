package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Repository.CMS.ShowcaseCurtidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListarCurtidasUsuarioUseCase {

    private final ShowcaseCurtidaRepository repository;

    public List<UUID> executar(UUID usuarioId) {
        return repository.findShowcaseIdsByUsuarioId(usuarioId);
    }
}