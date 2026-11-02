package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Repository.CMS.ShowcaseCurtidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListarCurtidasUsuarioUseCase {

    private final ShowcaseCurtidaRepository repository;

    // 🔥 Cacheia apenas os UUIDs curtidos por ESTE usuário
    @Cacheable(value = "showcases_curtidas_v1", key = "#usuarioId.toString()")
    @Transactional(readOnly = true)
    public List<UUID> executar(UUID usuarioId) {
        return repository.findShowcaseIdsByUsuarioId(usuarioId);
    }
}