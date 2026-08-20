package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Catalogo.Categoria;
import br.com.otica.otica_loja.UseCases.categorias.*;
import br.com.otica.otica_loja.dto.AtualizarCategoriaRequest;
import br.com.otica.otica_loja.dto.CategoriaResponseDTO;
import br.com.otica.otica_loja.dto.CriarCategoriaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/categorias")
public class AdminCategoriaController {

    @Autowired
    private CriarCategoriaUseCase criarCategoriaUseCase;

    @Autowired
    private AtualizarCategoriaUseCase atualizarCategoriaUseCase;

    @Autowired
    private BuscarCategoriaUseCase buscarCategoriaUseCase;

    @Autowired
    private ListarCategoriasUseCase listarCategoriasUseCase;

    @Autowired
    private ExcluirCategoriaUseCase excluirCategoriaUseCase;

    // 1. Criar uma nova categoria
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(@RequestBody CriarCategoriaRequest request) {
        Categoria novaCategoria = criarCategoriaUseCase.criar(
                request.nome(),
                request.slug(),
                request.descricao()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    // 2. Atualizar uma categoria existente
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizarCategoria(@PathVariable UUID id, @RequestBody AtualizarCategoriaRequest request) {
        Categoria categoriaAtualizada = atualizarCategoriaUseCase.atualizar(
                id,
                request.nome(),
                request.slug(),
                request.descricao(),
                request.ativo()
        );
        return ResponseEntity.ok(categoriaAtualizada);
    }

    // 3. Buscar categoria por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable UUID id) {
        Categoria categoria = buscarCategoriaUseCase.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    // 4. Buscar categoria por Slug
    @GetMapping("/slug/{slug}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Categoria> buscarPorSlug(@PathVariable String slug) {
        Categoria categoria = buscarCategoriaUseCase.buscarPorSlug(slug);
        return ResponseEntity.ok(categoria);
    }

    // 5. Listar todas as categorias (Geral)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        List<CategoriaResponseDTO> categorias = listarCategoriasUseCase.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    // 6. Listar apenas as categorias ativas
    @GetMapping("/ativas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<CategoriaResponseDTO>> listarAtivas() {
        List<CategoriaResponseDTO> categorias = listarCategoriasUseCase.listarAtivas();
        return ResponseEntity.ok(categorias);
    }

    // 7. Listar apenas as categorias inativas
    @GetMapping("/inativas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<CategoriaResponseDTO>> listarInativas() {
        List<CategoriaResponseDTO> categorias = listarCategoriasUseCase.listarInativas();
        return ResponseEntity.ok(categorias);
    }

    // 8. Soft Delete (Inativa a categoria pelo ID)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirSoft(@PathVariable UUID id) {
        excluirCategoriaUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // 9. Hard Delete (Apaga o registro definitivamente do banco)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/definitivo")
    public ResponseEntity<Void> excluirDefinitivo(@PathVariable UUID id) {
        excluirCategoriaUseCase.excluirDefinitivo(id);
        return ResponseEntity.noContent().build();
    }
}