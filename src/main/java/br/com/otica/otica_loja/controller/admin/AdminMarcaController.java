package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Catalogo.Marca;
import br.com.otica.otica_loja.UseCases.marcas.*;
import br.com.otica.otica_loja.dto.AtualizarMarcaRequestDTO;
import br.com.otica.otica_loja.dto.CriarMarcaRequestDTO;
import br.com.otica.otica_loja.dto.MarcaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/marcas")
public class AdminMarcaController {

    @Autowired
    private CriarMarcaUseCase criarMarcaUseCase;

    @Autowired
    private BuscarMarcaUseCase buscarMarcaUseCase;

    @Autowired
    private AtualizarMarcaUseCase atualizarMarcaUseCase;

    @Autowired
    private ListarMarcasUseCase listarMarcasUseCase;

    @Autowired
    private ExcluirMarcaUseCase excluirMarcaUseCase;

    // 1. Criar uma nova marca - EXCLUSIVO ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Marca> criarMarca(@ModelAttribute CriarMarcaRequestDTO request) throws IOException {
        Marca novaMarca = criarMarcaUseCase.criar(
                request.nome(),
                request.slug(),
                request.descricao(),
                request.logoFile(),
                request.logoUrl(),
                request.bannerFile(),
                request.bannerUrl()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(novaMarca);
    }

    // 2. Atualizar uma marca existente - EXCLUSIVO ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Marca> atualizarMarca(@PathVariable UUID id, @ModelAttribute AtualizarMarcaRequestDTO request) throws IOException {
        Marca marcaAtualizada = atualizarMarcaUseCase.atualizar(
                id,
                request.nome(),
                request.slug(),
                request.descricao(),
                request.ativo(),
                request.logoFile(),
                request.logoUrl(),
                request.bannerFile(),
                request.bannerUrl()
        );
        return ResponseEntity.ok(marcaAtualizada);
    }

    // 3. Buscar uma marca específica por ID - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Marca> buscarPorId(@PathVariable UUID id) {
        Marca marca = buscarMarcaUseCase.buscarPorId(id);
        return ResponseEntity.ok(marca);
    }

    // 4. Buscar uma marca específica por Slug - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<Marca> buscarPorSlug(@PathVariable String slug) {
        Marca marca = buscarMarcaUseCase.buscarPorSlug(slug);
        return ResponseEntity.ok(marca);
    }

    // 5. Listar todas as marcas - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @GetMapping("/todas")
    public ResponseEntity<List<MarcaResponseDTO>> listarTodas() {
        List<MarcaResponseDTO> marcas = listarMarcasUseCase.listarTodas();
        return ResponseEntity.ok(marcas);
    }

    // 6. Listar apenas as marcas não deletadas - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @GetMapping
    public ResponseEntity<List<MarcaResponseDTO>> listarNaoDeletadas() {
        List<MarcaResponseDTO> marcas = listarMarcasUseCase.listarNaoDeletadas();
        return ResponseEntity.ok(marcas);
    }

    // 7. Listar apenas as marcas que estão ativas - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @GetMapping("/ativas")
    public ResponseEntity<List<MarcaResponseDTO>> listarAtivas() {
        List<MarcaResponseDTO> marcas = listarMarcasUseCase.listarAtivas();
        return ResponseEntity.ok(marcas);
    }

    // 8. Listar apenas as marcas pausadas/inativas - ADMIN e GERENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @GetMapping("/inativas")
    public ResponseEntity<List<MarcaResponseDTO>> listarInativas() {
        List<MarcaResponseDTO> marcas = listarMarcasUseCase.listarInativas();
        return ResponseEntity.ok(marcas);
    }

    // 9. Soft Delete - EXCLUSIVO ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirSoft(@PathVariable UUID id) {
        excluirMarcaUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // 10. Hard Delete - EXCLUSIVO ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/definitivo")
    public ResponseEntity<Void> excluirDefinitivo(@PathVariable UUID id) {
        excluirMarcaUseCase.excluirDefinitivo(id);
        return ResponseEntity.noContent().build();
    }
}