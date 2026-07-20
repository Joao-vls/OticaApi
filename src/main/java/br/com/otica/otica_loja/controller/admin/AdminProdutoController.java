package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.UseCases.produtos.CriarProdutoUseCase;
import br.com.otica.otica_loja.UseCases.produtos.ListarProdutosUseCase;
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
    private final ListarProdutosUseCase listarProdutosUseCase;

    // Lista todos os produtos cadastrados no sistema para o lojista gerenciar
    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() {
        List<Produto> produtos = listarProdutosUseCase.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    // Cria um novo produto (Multipart com suporte a imagens, vídeos, miniaturas e arquivos 3D)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produto> criarProduto(
            @RequestPart("produto") ProdutoRequestDTO produtoDto,
            @RequestPart(value = "imagens", required = false) List<MultipartFile> imagens,
            @RequestPart(value = "videos", required = false) List<MultipartFile> videos,
            @RequestPart(value = "thumbnails", required = false) List<MultipartFile> thumbnails,
            @RequestPart(value = "arquivos3d", required = false) List<MultipartFile> arquivos3d
    ) throws IOException {

        Produto novoProduto = criarProdutoUseCase.criar(produtoDto, imagens, videos, thumbnails, arquivos3d);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    // Atualiza um produto existente (Ajustado para receber String provisória conforme seu mock)
    @PutMapping("/{id}")
    public String atualizarProduto(@PathVariable java.util.UUID id, @RequestBody String produto) {
        return "produto updated (id=" + id + "): " + produto;
    }

    // Deleta um produto existente (Ajustado o tipo do ID para UUID condizente com a entidade)
    @DeleteMapping("/{id}")
    public String deletarProduto(@PathVariable java.util.UUID id) {
        return "produto deletado (id=" + id + ")";
    }
}