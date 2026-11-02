package br.com.otica.otica_loja.controller;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.UseCases.produtos.*;
import br.com.otica.otica_loja.dto.FiltrosDisponiveisDTO;
import br.com.otica.otica_loja.dto.ProdutoDetalheDTO;
import br.com.otica.otica_loja.mapper.ProdutoDetalheMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    // 🔥 INJETANDO O MAPPER PARA TRANSFORMAR TUDO EM DTO LIMPO
    private final ProdutoDetalheMapper produtoDetalheMapper;

    @GetMapping("/pesquisar")
    public ResponseEntity<List<ProdutoDetalheDTO>> pesquisar(@RequestParam(value = "termo", required = false) String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Produto> resultados = pesquisarProdutosUseCase.pesquisarPorTextoLivre(termo);
        // Transforma a lista de entidades brutas em lista de DTOs
        return ResponseEntity.ok(resultados.stream().map(produtoDetalheMapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/filtros")
    public ResponseEntity<FiltrosDisponiveisDTO> obterFiltros() {
        FiltrosDisponiveisDTO filtros = obterFiltrosDisponiveisUseCase.executar();
        return ResponseEntity.ok(filtros);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoDetalheDTO>> listarTodosAtivos() {
        List<Produto> produtos = listarProdutosUseCase.listarAtivos();
        return ResponseEntity.ok(produtos.stream().map(produtoDetalheMapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ProdutoDetalheDTO>> listarTodos() {
        List<Produto> produtos = listarProdutosUseCase.listarTodos();
        return ResponseEntity.ok(produtos.stream().map(produtoDetalheMapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/destaques")
    public ResponseEntity<List<ProdutoDetalheDTO>> destaques() {
        List<Produto> produtosDestaque = listarProdutosUseCase.listarEmDestaque();
        return ResponseEntity.ok(produtosDestaque.stream().map(produtoDetalheMapper::toDTO).collect(Collectors.toList()));
    }

    // 🔄 ÚNICO endpoint unificado para tratar Slug ou ID principal (UUID) do produto
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

    // Rota específica para variantes (fallback do Angular)
    @GetMapping("/variante/{varianteId}")
    public ResponseEntity<ProdutoDetalheDTO> produtoPorVarianteId(@PathVariable String varianteId) {
        try {
            // 🔥 AGORA DEVOLVE O DTO E NÃO QUEBRA O JACKSON
            ProdutoDetalheDTO produto = buscarProdutoPorVarianteUseCase.executar(varianteId);
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