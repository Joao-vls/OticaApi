package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.UseCases.produtos.CriarProdutoUseCase;
import br.com.otica.otica_loja.UseCases.produtos.ListarProdutosUseCase; // Injetado
import br.com.otica.otica_loja.dto.ProdutoRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/produtos")
@RequiredArgsConstructor
public class AdminProdutoController {

    private final CriarProdutoUseCase criarProdutoUseCase;
    private final ListarProdutosUseCase listarProdutosUseCase; // Adicionado para gerenciar listagens

    // Lista todos os produtos cadastrados no sistema para o lojista gerenciar
    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() {
        List<Produto> produtos = listarProdutosUseCase.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    // Cria um novo produto (Multipart)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produto> criarProduto(
            @RequestPart("produto") ProdutoRequestDTO produtoDto,
            @RequestPart(value = "arquivos", required = false) List<MultipartFile> arquivos
    ) throws IOException {
        Produto novoProduto = criarProdutoUseCase.criar(produtoDto, arquivos);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    // Atualiza um produto existente (Ajustado para receber String provisória conforme seu mock)
    @PutMapping("/{id}")
    public String atualizarProduto(@PathVariable java.util.UUID id, @RequestBody String produto) {
        return "produto atualizado (id=" + id + "): " + produto;
    }

    // Deleta um produto existente (Ajustado o tipo do ID para UUID condizente com a entidade)
    @DeleteMapping("/{id}")
    public String deletarProduto(@PathVariable java.util.UUID id) {
        return "produto deletado (id=" + id + ")";
    }
}