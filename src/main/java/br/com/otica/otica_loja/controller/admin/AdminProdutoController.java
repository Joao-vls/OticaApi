package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.UseCases.produtos.AtualizarProdutoUseCase;
import br.com.otica.otica_loja.UseCases.produtos.CriarProdutoUseCase;
import br.com.otica.otica_loja.UseCases.produtos.DeletarVarianteUseCase;
import br.com.otica.otica_loja.UseCases.produtos.ExcluirProdutoUseCase;
import br.com.otica.otica_loja.UseCases.produtos.ExcluirProdutoVarianteUseCase;
import br.com.otica.otica_loja.UseCases.produtos.ListarProdutosUseCase;
import br.com.otica.otica_loja.dto.ProdutoRequestDTO;
import br.com.otica.otica_loja.dto.ProdutoUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/produtos")
@RequiredArgsConstructor
public class AdminProdutoController {

    private final CriarProdutoUseCase criarProdutoUseCase;
    private final ListarProdutosUseCase listarProdutosUseCase;
    private final AtualizarProdutoUseCase atualizarProdutoUseCase;
    private final ExcluirProdutoUseCase excluirProdutoUseCase;
    private final DeletarVarianteUseCase deletarVarianteUseCase;
    private final ExcluirProdutoVarianteUseCase excluirProdutoVarianteUseCase; // Mantido apenas para mídias avulsas se necessário

    // GERENTE e ADMIN podem visualizar a lista
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<Produto>> listarProdutos() {
        List<Produto> produtos = listarProdutosUseCase.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    // EXCLUSIVO ADMIN: Criar produtos
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
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

    // EXCLUSIVO ADMIN: Atualizar produtos
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Produto> atualizarProduto(
            @PathVariable UUID id,
            @RequestPart("produto") ProdutoUpdateRequestDTO produtoDto,
            MultipartHttpServletRequest request
    ) throws IOException {

        Produto produtoAtualizado = atualizarProdutoUseCase.atualizar(id, produtoDto, request);
        return ResponseEntity.ok(produtoAtualizado);
    }

    // EXCLUSIVO ADMIN: Soft Delete de Produto
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarProduto(@PathVariable UUID id) {
        excluirProdutoUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // EXCLUSIVO ADMIN: Hard Delete de Produto (Limpa Cloudinary + BD)
    @DeleteMapping("/{id}/definitivo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarProdutoDefinitivo(@PathVariable UUID id) {
        excluirProdutoUseCase.excluirDefinitivo(id);
        return ResponseEntity.noContent().build();
    }

    // EXCLUSIVO ADMIN: Soft Delete de Variante
    @DeleteMapping("/variantes/{varianteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarVariante(@PathVariable UUID varianteId) {
        deletarVarianteUseCase.excluirSoft(varianteId);
        return ResponseEntity.noContent().build();
    }

    // EXCLUSIVO ADMIN: Hard Delete de Variante (Limpa Cloudinary + BD)
    @DeleteMapping("/variantes/{varianteId}/definitivo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarVarianteDefinitivo(@PathVariable UUID varianteId) {
        deletarVarianteUseCase.excluirDefinitivo(varianteId);
        return ResponseEntity.noContent().build();
    }

    // EXCLUSIVO ADMIN: Exclusão de Mídia Avulsa (Cloudinary + BD)
    @DeleteMapping("/midias/{midiaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarMidiaIndividual(@PathVariable UUID midiaId) {
        excluirProdutoVarianteUseCase.excluirMidiaIndividual(midiaId);
        return ResponseEntity.noContent().build();
    }
    // EXCLUSIVO ADMIN: Alternar Status Ativo/Inativo
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable UUID id,
            @RequestBody java.util.Map<String, Boolean> payload
    ) {
        Boolean ativo = payload.get("ativo");
        // Chame aqui o seu UseCase ou repositório para atualizar o status no banco
        // Exemplo: atualizarProdutoUseCase.atualizarStatus(id, ativo);
        return ResponseEntity.noContent().build();
    }
}