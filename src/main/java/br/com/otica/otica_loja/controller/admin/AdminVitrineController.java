package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Entity.CMS.VitrineProduto;
import br.com.otica.otica_loja.UseCases.cms.*;
import br.com.otica.otica_loja.dto.cms.VincularProdutoRequestDTO;
import br.com.otica.otica_loja.dto.cms.VitrineAdminRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/vitrines")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminVitrineController {

    private final CriarVitrineUseCase criarVitrineUseCase;
    private final AdicionarProdutoVitrineUseCase adicionarProdutoVitrineUseCase;
    private final ListarVitrinesUseCase listarVitrinesUseCase;
    private final AtualizarVitrineUseCase atualizarVitrineUseCase;
    private final ExcluirVitrineUseCase deletarVitrineUseCase;

    public AdminVitrineController(
            CriarVitrineUseCase criarVitrineUseCase,
            AdicionarProdutoVitrineUseCase adicionarProdutoVitrineUseCase,
            ListarVitrinesUseCase listarVitrinesUseCase,
            AtualizarVitrineUseCase atualizarVitrineUseCase,
            ExcluirVitrineUseCase deletarVitrineUseCase
    ) {
        this.criarVitrineUseCase = criarVitrineUseCase;
        this.adicionarProdutoVitrineUseCase = adicionarProdutoVitrineUseCase;
        this.listarVitrinesUseCase = listarVitrinesUseCase;
        this.atualizarVitrineUseCase = atualizarVitrineUseCase;
        this.deletarVitrineUseCase = deletarVitrineUseCase;
    }

    // Gerente e Admin podem visualizar as vitrines
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<Vitrine>> listarVitrines() {
        List<Vitrine> vitrines = listarVitrinesUseCase.executar();
        return ResponseEntity.ok(vitrines);
    }

    // Apenas Admin pode criar
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarVitrine(@RequestBody @Valid VitrineAdminRequestDTO dto) {
        try {
            CriarVitrineUseCase.Command command = new CriarVitrineUseCase.Command(
                    dto.nome(),
                    dto.slug(),
                    dto.titulo(),
                    dto.subtitulo(),
                    dto.ordem(),
                    dto.ativo(),
                    dto.produtosIds()
            );

            Vitrine novaVitrine = criarVitrineUseCase.executar(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaVitrine);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

    // Apenas Admin pode atualizar
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarVitrine(@PathVariable UUID id, @RequestBody @Valid VitrineAdminRequestDTO dto) {
        try {
            AtualizarVitrineUseCase.Command command = new AtualizarVitrineUseCase.Command(
                    dto.nome(),
                    dto.slug(),
                    dto.titulo(),
                    dto.subtitulo(),
                    dto.ordem(),
                    dto.ativo(),
                    dto.produtosIds()
            );

            Vitrine vitrineAtualizada = atualizarVitrineUseCase.executar(id, command);
            return ResponseEntity.ok(vitrineAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

    // Apenas Admin pode deletar
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletarVitrine(@PathVariable UUID id) {
        try {
            deletarVitrineUseCase.executar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Apenas Admin pode vincular produtos diretamente
    @PostMapping("/{id}/produtos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> vincularProduto(
            @PathVariable UUID id,
            @RequestBody @Valid VincularProdutoRequestDTO dto
    ) {
        try {
            VitrineProduto vinculo = adicionarProdutoVitrineUseCase.adicionarProduto(
                    id,
                    dto.produtoId(),
                    dto.ordem()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(vinculo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }
}