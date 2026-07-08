package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Catalogo.Categoria;
import br.com.otica.otica_loja.UseCases.categorias.*;
import br.com.otica.otica_loja.dto.AtualizarCategoriaRequest;
import br.com.otica.otica_loja.dto.CriarCategoriaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizarCategoria(@PathVariable UUID id, @RequestBody AtualizarCategoriaRequest request) { // <-- Corrigido aqui
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
    public ResponseEntity<Categoria> buscarPorId(@PathVariable UUID id) {
        Categoria categoria = buscarCategoriaUseCase.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    // 4. Buscar categoria por Slug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<Categoria> buscarPorSlug(@PathVariable String slug) {
        Categoria categoria = buscarCategoriaUseCase.buscarPorSlug(slug);
        return ResponseEntity.ok(categoria);
    }

    // 5. Listar todas as categorias (Geral)
    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodas() {
        List<Categoria> categorias = listarCategoriasUseCase.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    // 6. Listar apenas as categorias ativas
    @GetMapping("/ativas")
    public ResponseEntity<List<Categoria>> listarAtivas() {
        List<Categoria> categorias = listarCategoriasUseCase.listarAtivas();
        return ResponseEntity.ok(categorias);
    }

    // 7. Listar apenas as categorias inativas
    @GetMapping("/inativas")
    public ResponseEntity<List<Categoria>> listarInativas() {
        List<Categoria> categorias = listarCategoriasUseCase.listarInativas();
        return ResponseEntity.ok(categorias);
    }

    // 8. Soft Delete (Inativa a categoria pelo ID)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirSoft(@PathVariable UUID id) {
        excluirCategoriaUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // 9. Hard Delete (Apaga o registro definitivamente do banco)
    @DeleteMapping("/{id}/definitivo")
    public ResponseEntity<Void> excluirDefinitivo(@PathVariable UUID id) {
        excluirCategoriaUseCase.excluirDefinitivo(id);
        return ResponseEntity.noContent().build();
    }
}