package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Carrinho.Carrinho;
import br.com.otica.otica_loja.Entity.Carrinho.CarrinhoItem;
import br.com.otica.otica_loja.Repository.Carrinho.CarrinhoItemRepository;
import br.com.otica.otica_loja.UseCases.carrinho.*;
import br.com.otica.otica_loja.dto.AdicionarItemDTO;
import br.com.otica.otica_loja.dto.AtualizarQuantidadeItemDTO;
import br.com.otica.otica_loja.dto.CarrinhoItemResponseDTO;
import br.com.otica.otica_loja.dto.CarrinhoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cliente/carrinho")
public class CarrinhoController {

    @Autowired
    private BuscarCarrinhoUseCase buscarCarrinhoUseCase;

    @Autowired
    private CriarCarrinhoUseCase criarCarrinhoUseCase;

    @Autowired
    private AdicionarItemCarrinhoUseCase adicionarItemCarrinhoUseCase;

    @Autowired
    private AtualizarQuantidadeCarrinhoUseCase atualizarQuantidadeCarrinhoUseCase;

    @Autowired
    private RemoverItemCarrinhoUseCase removerItemCarrinhoUseCase;

    @Autowired
    private LimparCarrinhoUseCase limparCarrinhoUseCase;

    @Autowired
    private CarrinhoItemRepository carrinhoItemRepository;

    /**
     * Obtém o carrinho do cliente autenticado.
     * Caso o carrinho não exista, gera um novo automaticamente.
     */
    @GetMapping
    public ResponseEntity<?> obterCarrinho(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Carrinho carrinho = obterOuCriarCarrinho(usuarioLogado.getId());
        List<CarrinhoItem> itens = carrinhoItemRepository.findByCarrinho(carrinho);

        List<CarrinhoItemResponseDTO> itensDTO = itens.stream()
                .map(CarrinhoItemResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(CarrinhoResponseDTO.fromEntity(carrinho, itensDTO));
    }

    /**
     * Adiciona um novo item (variante) ao carrinho.
     */
    @PostMapping("/itens")
    public ResponseEntity<?> adicionarItem(@AuthenticationPrincipal Usuario usuarioLogado,
                                           @Valid @RequestBody AdicionarItemDTO dto) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            obterOuCriarCarrinho(usuarioLogado.getId());

            CarrinhoItem itemAdicionado = adicionarItemCarrinhoUseCase.adicionar(
                    usuarioLogado.getId(),
                    dto.varianteId(),
                    dto.quantidade()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CarrinhoItemResponseDTO.fromEntity(itemAdicionado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao adicionar item ao carrinho: " + e.getMessage()));
        }
    }

    /**
     * Atualiza a quantidade de uma variante já existente no carrinho.
     */
    @PutMapping("/itens/{varianteId}")
    public ResponseEntity<?> atualizarQuantidade(@AuthenticationPrincipal Usuario usuarioLogado,
                                                 @PathVariable UUID varianteId,
                                                 @Valid @RequestBody AtualizarQuantidadeItemDTO dto) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            CarrinhoItem itemAtualizado = atualizarQuantidadeCarrinhoUseCase.atualizar(
                    usuarioLogado.getId(),
                    varianteId,
                    dto.quantidade()
            );

            return ResponseEntity.ok(CarrinhoItemResponseDTO.fromEntity(itemAtualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao atualizar quantidade: " + e.getMessage()));
        }
    }

    /**
     * Remove uma variante do carrinho.
     */
    @DeleteMapping("/itens/{varianteId}")
    public ResponseEntity<?> removerItem(@AuthenticationPrincipal Usuario usuarioLogado,
                                         @PathVariable UUID varianteId) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            removerItemCarrinhoUseCase.remover(usuarioLogado.getId(), varianteId);
            return ResponseEntity.ok(Map.of("message", "Item removido do carrinho com sucesso."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao remover item do carrinho: " + e.getMessage()));
        }
    }

    /**
     * Esvazia todos os itens do carrinho.
     */
    @DeleteMapping
    public ResponseEntity<?> limparCarrinho(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            limparCarrinhoUseCase.limpar(usuarioLogado.getId());
            return ResponseEntity.ok(Map.of("message", "Carrinho esvaziado com sucesso."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao limpar carrinho: " + e.getMessage()));
        }
    }

    /**
     * Garante o retorno do carrinho do cliente criando um caso ainda não exista.
     */
    private Carrinho obterOuCriarCarrinho(UUID usuarioId) {
        try {
            return buscarCarrinhoUseCase.buscar(usuarioId);
        } catch (IllegalArgumentException e) {
            return criarCarrinhoUseCase.criar(usuarioId);
        }
    }
}