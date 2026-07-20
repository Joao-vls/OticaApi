package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.UseCases.produtos.ListarProdutosUseCase;
import br.com.otica.otica_loja.UseCases.produtos.BuscarProdutoPorSlugUseCase; // 🔥 Novo Import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 🔥 Adicionado para evitar erros de CORS no Angular (ajuste a URL se necessário)
public class ProdutoController {

    private final ListarProdutosUseCase listarProdutosUseCase;
    private final BuscarProdutoPorSlugUseCase buscarProdutoPorSlugUseCase; // 🔥 Injetado

    // Lista os produtos ativos da loja (Filtro ideal para a vitrine principal)
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodosAtivos() {
        List<Produto> produtos = listarProdutosUseCase.listarAtivos();
        return ResponseEntity.ok(produtos);
    }

    // Busca os produtos marcados em destaque
    @GetMapping("/destaques")
    public ResponseEntity<List<Produto>> destaques() {
        List<Produto> produtosDestaque = listarProdutosUseCase.listarEmDestaque();
        return ResponseEntity.ok(produtosDestaque);
    }

    // 🔥 Corrigido: Agora retorna o Produto buscado do banco pelo Slug para alimentar o Angular
    @GetMapping("/{slug}")
    public ResponseEntity<Produto> produtoPorSlug(@PathVariable String slug) {
        Produto produto = buscarProdutoPorSlugUseCase.executar(slug);

        if (produto == null) {
            return ResponseEntity.notFound().build(); // Retorna 404 caso o slug não exista
        }

        return ResponseEntity.ok(produto);
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