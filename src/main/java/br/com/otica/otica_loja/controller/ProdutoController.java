package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.UseCases.produtos.ListarProdutosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor // Adicionado para injetar o UseCase via construtor do Lombok
public class ProdutoController {

    private final ListarProdutosUseCase listarProdutosUseCase;

    // Lista os produtos ativos da loja (Filtro ideal para a vitrine principal)
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodosAtivos() {
        List<Produto> produtos = listarProdutosUseCase.listarAtivos();
        return ResponseEntity.ok(produtos);
    }

    // Busca os produtos marcados em destaque
    @GetMapping("/destaques") // Corrigido: Removido o "/produtos" duplicado
    public ResponseEntity<List<Produto>> destaques() {
        List<Produto> produtosDestaque = listarProdutosUseCase.listarEmDestaque();
        return ResponseEntity.ok(produtosDestaque);
    }

    // Exibe os dados de um produto específico baseado no Slug da URL
    @GetMapping("/{slug}")
    public String produtoPorSlug(@PathVariable String slug) {
        // TODO: Criar um UseCase focado em buscar por Slug (ex: buscarPorSlug(slug))
        return "Retorno do produto com o slug: " + slug;
    }

    @GetMapping("/lancamentos") // Corrigido: Removido o "/produtos" duplicado
    public String lancamentos() {
        return "Mapear futuramente ordenado por criadoEm decrescente";
    }

    @GetMapping("/promocoes") // Corrigido: Removido o "/produtos" duplicado
    public String promocoes() {
        return "Mapear futuramente produtos com desconto ativo";
    }
}