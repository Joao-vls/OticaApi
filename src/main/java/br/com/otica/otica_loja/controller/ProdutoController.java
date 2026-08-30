package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.UseCases.produtos.*;
import br.com.otica.otica_loja.dto.FiltrosDisponiveisDTO;
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
    public ResponseEntity<List<Produto>> pesquisar(@RequestParam(value = "termo", required = false) String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Produto> resultados = pesquisarProdutosUseCase.pesquisarPorTextoLivre(termo);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/filtros")
    public ResponseEntity<FiltrosDisponiveisDTO> obterFiltros() {
        FiltrosDisponiveisDTO filtros = obterFiltrosDisponiveisUseCase.executar();
        return ResponseEntity.ok(filtros);
    }
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodosAtivos() {
        List<Produto> produtos = listarProdutosUseCase.listarAtivos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = listarTodosAtivosInativos();
        return ResponseEntity.ok(produtos);
    }

    private List<Produto> listarTodosAtivosInativos() {
        return listarProdutosUseCase.listarTodos();
    }

    @GetMapping("/destaques")
    public ResponseEntity<List<Produto>> destaques() {
        List<Produto> produtosDestaque = listarProdutosUseCase.listarEmDestaque();
        return ResponseEntity.ok(produtosDestaque);
    }

    // 🔄 ÚNICO endpoint unificado para tratar Slug ou ID principal (UUID) do produto
    @GetMapping("/{identificador}")
    public ResponseEntity<Produto> produtoPorSlugOuId(@PathVariable String identificador) {
        try {
            Produto produto;
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

    // Rota específica para variantes (mantida intacta)
    @GetMapping("/variante/{varianteId}")
    public ResponseEntity<Produto> produtoPorVarianteId(@PathVariable String varianteId) {
        try {
            Produto produto = buscarProdutoPorVarianteUseCase.executar(varianteId);
            return ResponseEntity.ok(produto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lancamentos")
    public String lancamentos() {
        return "Mapear futuramente ordenado por criadoEm decrescente";
    }

    @GetMapping("/promocoes")
    public String promocoes() {
        return "Mapear futuramente produtos com desconto ativo";
    }
}