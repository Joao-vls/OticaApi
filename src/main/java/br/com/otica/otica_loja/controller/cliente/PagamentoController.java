package br.com.otica.otica_loja.controller.cliente;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.UseCases.pagamentos.*;
import br.com.otica.otica_loja.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final CriarPagamentoPixUseCase criarPagamentoPixUseCase;
    private final CriarPagamentoCartaoUseCase criarPagamentoCartaoUseCase;
    private final CriarPagamentoBoletoUseCase criarPagamentoBoletoUseCase;
    private final CancelarPagamentoUseCase cancelarPagamentoUseCase;

    @PostMapping("/pix/{pedidoId}")
    @PreAuthorize("hasAnyRole('CLIENTE')")
    public ResponseEntity<PixPagamentoResponse> pagarComPix(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable UUID pedidoId,
            @RequestBody(required = false) Map<String, String> payload) {

        // Pega o e-mail do payload, ou usa o e-mail de cadastro do usuário logado por padrão
        String emailCliente = (payload != null && payload.containsKey("emailCliente"))
                ? payload.get("emailCliente")
                : usuarioLogado.getEmail();

        PixPagamentoResponse response = criarPagamentoPixUseCase.criarPagamento(
                pedidoId,
                usuarioLogado.getId(),
                emailCliente
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cartao/{pedidoId}")
    @PreAuthorize("hasAnyRole('CLIENTE')")
    public ResponseEntity<CartaoPagamentoResponse> pagarComCartao(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable UUID pedidoId,
            @RequestBody CartaoPagamentoRequest request) {

        // Passa o ID seguro do usuário logado diretamente para o UseCase
        CartaoPagamentoResponse response = criarPagamentoCartaoUseCase.criarPagamento(
                pedidoId,
                usuarioLogado.getId(),
                request
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/boleto")
    @PreAuthorize("hasAnyRole('CLIENTE')")
    public ResponseEntity<BoletoPagamentoResponse> pagarComBoleto(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody BoletoPagamentoRequest requestBody) {

        // 🔒 Segurança: Recriando o Record para forçar o ID e dados essenciais do usuário logado,
        // removendo os campos de endereço do DTO (o backend buscará o endereço padrão cadastrado).
        BoletoPagamentoRequest requestSeguro = new BoletoPagamentoRequest(
                requestBody.pedidoId(),
                usuarioLogado.getId(), // Injetado de forma segura do token
                requestBody.emailCliente() != null ? requestBody.emailCliente() : usuarioLogado.getEmail(),
                requestBody.nomeCliente(),
                requestBody.sobrenomeCliente(),
                requestBody.cpfCliente()
        );

        BoletoPagamentoResponse response = criarPagamentoBoletoUseCase.criarPagamento(requestSeguro);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancelar")
    @PreAuthorize("hasRole('ADMIN')") // Apenas administradores podem cancelar pagamentos à força
    public ResponseEntity<Void> cancelarPagamento(
            @AuthenticationPrincipal Usuario adminLogado,
            @RequestBody CancelarPagamentoRequest requestBody) {

        // 🔒 Segurança: Garante que o responsável pelo cancelamento é o admin autenticado
        CancelarPagamentoRequest requestSeguro = new CancelarPagamentoRequest(
                requestBody.pedidoId(),
                adminLogado.getId(), // ID do administrador extraído do token
                requestBody.estorno(),
                requestBody.motivo(),
                requestBody.idPagamentoMercadoPago()
        );

        cancelarPagamentoUseCase.cancelarPagamento(requestSeguro);

        return ResponseEntity.noContent().build(); // 204 No Content
    }
}