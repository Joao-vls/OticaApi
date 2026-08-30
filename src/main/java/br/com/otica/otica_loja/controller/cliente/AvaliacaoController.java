package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Avaliacao.ProdutoAvaliacao;
import br.com.otica.otica_loja.UseCases.avaliacoes.*;
import br.com.otica.otica_loja.dto.CriarAvaliacaoDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private CriarAvaliacaoUseCase criarAvaliacaoUseCase;

    @Autowired
    private VerificarAvaliacaoUsuarioProdutoUseCase verificarAvaliacaoUsuarioProdutoUseCase;

    @Autowired
    private VerificarCompraUsuarioUseCase verificarCompraUsuarioUseCase;

    @Autowired
    private ListarAvaliacoesProdutoUseCase listarAvaliacoesProdutoUseCase;

    @Autowired
    private DeletarAvaliacaoUsuarioUseCase deletarAvaliacaoUsuarioUseCase;

    /**
     * Permite avaliar apenas se o pedido contendo o produto já foi ENTREGUE.
     */
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> criarAvaliacao(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @Valid @RequestBody CriarAvaliacaoDTO dto) {

        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID usuarioId = usuarioLogado.getId();

        // 1. Validar se o produto foi efetivamente ENTREGUE ao usuário
        boolean entregue = verificarCompraUsuarioUseCase.usuarioComprouECompletouProduto(dto.produtoId(), usuarioId);
        if (!entregue) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Você só pode avaliar produtos de pedidos que já foram entregues."));
        }

        // 2. Validar se o usuário já avaliou o produto anteriormente
        boolean jaAvaliou = verificarAvaliacaoUsuarioProdutoUseCase.jaAvaliou(dto.produtoId(), usuarioId);
        if (jaAvaliou) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Você já enviou uma avaliação para este produto."));
        }

        try {
            ProdutoAvaliacao avaliacao = criarAvaliacaoUseCase.criar(
                    dto.produtoId(),
                    usuarioId,
                    usuarioLogado.getNome(),
                    dto.nota(),
                    dto.titulo(),
                    dto.texto(),
                    dto.imagemPath()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(avaliacao);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Endpoint público para listar avaliações aprovadas do produto.
     */
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<ProdutoAvaliacao>> listarAprovadasPorProduto(@PathVariable UUID produtoId) {
        List<ProdutoAvaliacao> avaliacoes = listarAvaliacoesProdutoUseCase.listarAprovadas(produtoId);
        return ResponseEntity.ok(avaliacoes);
    }

    /**
     * Endpoint para consultar se o usuário logado pode avaliar o produto.
     */
    @GetMapping("/pode-avaliar/{produtoId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> podeAvaliar(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable UUID produtoId) {

        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID usuarioId = usuarioLogado.getId();
        boolean entregue = verificarCompraUsuarioUseCase.usuarioComprouECompletouProduto(produtoId, usuarioId);
        boolean jaAvaliou = verificarAvaliacaoUsuarioProdutoUseCase.jaAvaliou(produtoId, usuarioId);

        return ResponseEntity.ok(Map.of(
                "podeAvaliar", entregue && !jaAvaliou,
                "entregue", entregue,
                "jaAvaliou", jaAvaliou
        ));
    }

    /**
     * Permite que o usuário autenticado remova a sua própria avaliação de um produto.
     */
    @DeleteMapping("/produto/{produtoId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> deletarMinhaAvaliacao(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable UUID produtoId) {

        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            deletarAvaliacaoUsuarioUseCase.deletar(produtoId, usuarioLogado.getId());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}