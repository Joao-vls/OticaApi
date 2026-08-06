package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.Entity.Catalogo.Categoria;
import br.com.otica.otica_loja.UseCases.categorias.ListarCategoriasUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final ListarCategoriasUseCase listarCategoriasUseCase;

    // Lista todas as categorias ativas
    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        List<Categoria> categorias = listarCategoriasUseCase.listarAtivas();
        return ResponseEntity.ok(categorias);
    }

    // Detalhes de uma categoria específica
    @GetMapping("/{slug}")
    public String detalhesCategoria(@PathVariable String slug) {
        return "detalhes da categoria: " + slug;
    }

    // Produtos de uma categoria específica
    @GetMapping("/{slug}/produtos")
    public String produtosCategoria(@PathVariable String slug) {
        return "produtos da categoria: " + slug;
    }
}