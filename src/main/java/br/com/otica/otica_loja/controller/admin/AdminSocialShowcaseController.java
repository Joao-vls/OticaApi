package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.CMS.SocialShowcase;
import br.com.otica.otica_loja.Repository.CMS.SocialShowcaseRepository;
import br.com.otica.otica_loja.UseCases.cms.CriarSocialShowcaseUseCase;
import br.com.otica.otica_loja.UseCases.cms.ExcluirSocialShowcaseUseCase;
import br.com.otica.otica_loja.dto.SocialShowcaseRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/social-showcase")
@RequiredArgsConstructor
public class AdminSocialShowcaseController {

    private final SocialShowcaseRepository repository;
    private final CriarSocialShowcaseUseCase criarSocialShowcaseUseCase;
    private final ExcluirSocialShowcaseUseCase excluirSocialShowcaseUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<SocialShowcase>> listarTodos() {
        return ResponseEntity.ok(repository.findAllByOrderByOrdemAsc());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SocialShowcase> criar(
            @RequestPart("dados") SocialShowcaseRequestDTO dto,
            @RequestPart("video") MultipartFile video,
            @RequestPart("thumbnail") MultipartFile thumbnail
    ) throws IOException {
        SocialShowcase criado = criarSocialShowcaseUseCase.executar(dto, video, thumbnail);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> alternarStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> payload
    ) {
        SocialShowcase showcase = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showcase não encontrado."));

        Boolean ativo = payload.get("ativo");
        if (ativo != null) {
            showcase.setAtivo(ativo);
            repository.save(showcase);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirSocialShowcaseUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}