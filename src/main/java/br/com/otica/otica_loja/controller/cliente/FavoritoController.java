package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Avaliacao.Favorito;
import br.com.otica.otica_loja.UseCases.favoritos.AdicionarFavoritoUseCase;
import br.com.otica.otica_loja.UseCases.favoritos.ListarFavoritosUseCase;
import br.com.otica.otica_loja.UseCases.favoritos.RemoverFavoritoUseCase;
import br.com.otica.otica_loja.dto.FavoritoDTO;
import br.com.otica.otica_loja.dto.FavoritoResponseDTO;
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
@RequestMapping("/api/cliente/favoritos")
public class FavoritoController {

    @Autowired
    private AdicionarFavoritoUseCase adicionarFavoritoUseCase;

    @Autowired
    private ListarFavoritosUseCase listarFavoritosUseCase;

    @Autowired
    private RemoverFavoritoUseCase removerFavoritoUseCase;

    /**
     * Lista todos os produtos favoritados pelo cliente autenticado.
     */
    @GetMapping
    public ResponseEntity<?> listarFavoritos(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Favorito> favoritos = listarFavoritosUseCase.listarPorUsuario(usuarioLogado.getId());
            List<FavoritoResponseDTO> responseDTOs = favoritos.stream()
                    .map(FavoritoResponseDTO::fromEntity)
                    .toList();

            return ResponseEntity.ok(responseDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao buscar favoritos: " + e.getMessage()));
        }
    }

    /**
     * Adiciona um produto à lista de favoritos do cliente autenticado.
     */
    @PostMapping
    public ResponseEntity<?> adicionarFavorito(@AuthenticationPrincipal Usuario usuarioLogado,
                                               @Valid @RequestBody FavoritoDTO dto) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Favorito favorito = adicionarFavoritoUseCase.adicionar(usuarioLogado.getId(), dto.produtoId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(FavoritoResponseDTO.fromEntity(favorito));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao adicionar produto aos favoritos: " + e.getMessage()));
        }
    }

    /**
     * Remove um produto da lista de favoritos do cliente autenticado.
     */
    @DeleteMapping("/{produtoId}")
    public ResponseEntity<?> removerFavorito(@AuthenticationPrincipal Usuario usuarioLogado,
                                             @PathVariable UUID produtoId) {
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            removerFavoritoUseCase.remover(usuarioLogado.getId(), produtoId);
            return ResponseEntity.ok(Map.of("message", "Produto removido dos favoritos com sucesso."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao remover produto dos favoritos: " + e.getMessage()));
        }
    }
}