package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.UseCases.produtos.*;
import br.com.otica.otica_loja.dto.FiltrosDisponiveisDTO;
import br.com.otica.otica_loja.dto.ProdutoDetalheDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ListarProdutosUseCase listarProdutosUseCase;
    private final BuscarProdutoPorSlugUseCase buscarProdutoPorSlugUseCase;
    private final BuscarProdutoPorIdUseCase buscarProdutoPorIdUseCase;
    private final PesquisarProdutosUseCase pesquisarProdutosUseCase;
    private final BuscarProdutoPorVarianteUseCase buscarProdutoPorVarianteUseCase;
    private final ObterFiltrosDisponiveisUseCase obterFiltrosDisponiveisUseCase;

    @GetMapping("/pesquisar")
    public ResponseEntity<List<ProdutoDetalheDTO>> pesquisar(@RequestParam(value = "termo", required = false) String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(pesquisarProdutosUseCase.pesquisarPorTextoLivre(termo));
    }

    @GetMapping("/filtros")
    public ResponseEntity<FiltrosDisponiveisDTO> obterFiltros() {
        FiltrosDisponiveisDTO filtros = obterFiltrosDisponiveisUseCase.executar();
        return ResponseEntity.ok(filtros);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoDetalheDTO>> listarTodosAtivos() {
        return ResponseEntity.ok(listarProdutosUseCase.listarAtivos());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ProdutoDetalheDTO>> listarTodos() {
        return ResponseEntity.ok(listarProdutosUseCase.listarTodos());
    }

    @GetMapping("/destaques")
    public ResponseEntity<List<ProdutoDetalheDTO>> destaques() {
        return ResponseEntity.ok(listarProdutosUseCase.listarEmDestaque());
    }

    @GetMapping("/{identificador}")
    public ResponseEntity<ProdutoDetalheDTO> produtoPorSlugOuId(@PathVariable String identificador) {
        try {
            ProdutoDetalheDTO produto;
            try {
                java.util.UUID produtoId = java.util.UUID.fromString(identificador);
                produto = buscarProdutoPorIdUseCase.buscarPorId(produtoId);
            } catch (IllegalArgumentException uuidEx) {
                produto = buscarProdutoPorSlugUseCase.executar(identificador);
            }
            return ResponseEntity.ok(produto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/variante/{varianteId}")
    public ResponseEntity<ProdutoDetalheDTO> produtoPorVarianteId(@PathVariable String varianteId) {
        try {
            return ResponseEntity.ok(buscarProdutoPorVarianteUseCase.executar(varianteId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}