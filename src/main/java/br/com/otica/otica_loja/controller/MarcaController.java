package br.com.otica.otica_loja.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/marcas")
public class MarcaController {

    // Lista todas as marcas
    @GetMapping
    public String listarMarcas() {
        return "lista de marcas";
    }

    // Detalhes de uma marca específica
    @GetMapping("/{slug}")
    public String detalhesMarca(@PathVariable String slug) {
        return "detalhes da marca: " + slug;
    }

    // Produtos de uma marca específica
    @GetMapping("/{slug}/produtos")
    public String produtosMarca(@PathVariable String slug) {
        return "produtos da marca: " + slug;
    }
}
