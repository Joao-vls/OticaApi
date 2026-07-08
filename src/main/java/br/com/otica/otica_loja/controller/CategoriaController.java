package br.com.otica.otica_loja.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    // Lista todas as categorias
    @GetMapping
    public String listarCategorias() {
        return "lista de categorias";
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
