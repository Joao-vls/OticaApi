package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.UseCases.produtos.BuscarProdutoPorSlugUseCase;
import br.com.otica.otica_loja.UseCases.produtos.ListarProdutosUseCase;
import br.com.otica.otica_loja.UseCases.produtos.PesquisarProdutosUseCase;
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
    private final PesquisarProdutosUseCase pesquisarProdutosUseCase;

    @GetMapping("/pesquisar")
    public ResponseEntity<List<Produto>> pesquisar(@RequestParam(value = "termo", required = false) String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Produto> resultados = pesquisarProdutosUseCase.pesquisarPorTextoLivre(termo);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodosAtivos() {
        List<Produto> produtos = listarProdutosUseCase.listarAtivos();
        return ResponseEntity.ok(produtos);
    }
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = listarProdutosUseCase.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/destaques")
    public ResponseEntity<List<Produto>> destaques() {
        List<Produto> produtosDestaque = listarProdutosUseCase.listarEmDestaque();
        return ResponseEntity.ok(produtosDestaque);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Produto> produtoPorSlug(@PathVariable String slug) {
        try {
            Produto produto = buscarProdutoPorSlugUseCase.executar(slug);
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