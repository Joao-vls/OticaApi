package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.UseCases.marcas.ListarMarcasUseCase;
import br.com.otica.otica_loja.dto.MarcaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/marcas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MarcaController {

    private final ListarMarcasUseCase listarMarcasUseCase;

    // Lista todas as marcas ativas no e-commerce
    @GetMapping
    public ResponseEntity<List<MarcaResponseDTO>> listarMarcas() {
        List<MarcaResponseDTO> marcas = listarMarcasUseCase.listarAtivas();
        return ResponseEntity.ok(marcas);
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