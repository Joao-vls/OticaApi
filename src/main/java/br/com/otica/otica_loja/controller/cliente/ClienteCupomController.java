package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.UseCases.cupons.ListarCuponsDisponiveisClienteUseCase;
import br.com.otica.otica_loja.UseCases.cupons.ValidarCupomUseCase;
import br.com.otica.otica_loja.dto.CupomDisponivelResponse;
import br.com.otica.otica_loja.dto.ValidarCupomClienteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cliente/cupons")
public class ClienteCupomController {

    @Autowired
    private ValidarCupomUseCase validarCupomUseCase;

    @Autowired
    private ListarCuponsDisponiveisClienteUseCase listarCuponsDisponiveisClienteUseCase;

    @PostMapping("/validar")
    public ResponseEntity<?> validarCupomParaCliente(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody ValidarCupomClienteRequest request) {

        if (usuarioLogado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            // 1. Valida normalmente
            Cupom cupom = validarCupomUseCase.validar(
                    request.codigo(),
                    request.valorPedido(),
                    usuarioLogado.getId(),
                    request.produtosIds()
            );

            // 2. Coleta os IDs exatos que o Angular precisa saber que ganharam desconto
            Set<UUID> variantesAprovadas = validarCupomUseCase.obterVariantesElegiveis(cupom, request.produtosIds());

            Set<UUID> todosIdsParaOFront = new HashSet<>();
            if (cupom.getProdutosIdsEspecificos() != null) {
                todosIdsParaOFront.addAll(cupom.getProdutosIdsEspecificos());
            }
            todosIdsParaOFront.addAll(variantesAprovadas);

            // 3. Monta uma resposta blindada para o Angular
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", cupom.getCodigo());
            response.put("tipo", cupom.getTipo());
            response.put("valor", cupom.getValor());
            response.put("limiteItensPorPedido", cupom.getLimiteItensPorPedido() != null ? cupom.getLimiteItensPorPedido() : 999999);
            response.put("produtosIdsEspecificos", todosIdsParaOFront);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @GetMapping("/disponiveis")
    public ResponseEntity<List<CupomDisponivelResponse>> listarCuponsDisponiveis(
            @AuthenticationPrincipal Usuario usuarioLogado) {

        if (usuarioLogado == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<CupomDisponivelResponse> cupons = listarCuponsDisponiveisClienteUseCase
                .listarParaCliente(usuarioLogado.getId());

        return ResponseEntity.ok(cupons);
    }
}