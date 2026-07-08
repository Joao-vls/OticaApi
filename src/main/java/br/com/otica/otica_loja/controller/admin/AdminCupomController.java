package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import br.com.otica.otica_loja.UseCases.cupons.*;
import br.com.otica.otica_loja.dto.AplicarCupomRequest;
import br.com.otica.otica_loja.dto.AtualizarCupomRequest;
import br.com.otica.otica_loja.dto.CriarCupomRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/admin/cupons")
public class AdminCupomController {

    @Autowired
    private CriarCupomUseCase criarCupomUseCase;

    @Autowired
    private AtualizarCupomUseCase atualizarCupomUseCase;

    @Autowired
    private ExcluirCupomUseCase excluirCupomUseCase;

    @Autowired
    private ValidarCupomUseCase validarCupomUseCase;

    @Autowired
    private AplicarCupomUseCase aplicarCupomUseCase;

    // 1. Criar um novo cupom
    @PostMapping
    public ResponseEntity<Cupom> criarCupom(@RequestBody CriarCupomRequest request) {
        Cupom novoCupom = criarCupomUseCase.criar(
                request.codigo(),
                request.descricao(),
                request.tipo(),
                request.valor(),
                request.valorMinimoPedido(),
                request.quantidadeTotal(),
                request.dataInicio(),
                request.dataFim()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCupom);
    }

    // 2. Atualizar um cupom existente
    @PutMapping("/{id}")
    public ResponseEntity<Cupom> atualizarCupom(@PathVariable UUID id, @RequestBody AtualizarCupomRequest request) {
        Cupom cupomAtualizado = atualizarCupomUseCase.atualizar(
                id,
                request.codigo(),
                request.descricao(),
                request.tipo(),
                request.valor(),
                request.valorMinimoPedido(),
                request.quantidadeTotal(),
                request.dataInicio(),
                request.dataFim(),
                request.ativo()
        );
        return ResponseEntity.ok(cupomAtualizado);
    }

    // 3. Deletar definitivamente um cupom (Hard Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCupom(@PathVariable UUID id) {
        excluirCupomUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // 4. Apenas Validar as regras do cupom sem computar uso
    @GetMapping("/validar")
    public ResponseEntity<Cupom> validarCupom(@RequestParam String codigo, @RequestParam BigDecimal valorPedido) {
        Cupom cupom = validarCupomUseCase.validar(codigo, valorPedido);
        return ResponseEntity.ok(cupom);
    }

    // 5. Aplicar o cupom (Processa as regras de desconto e adiciona +1 no uso)
    @PostMapping("/aplicar")
    public ResponseEntity<BigDecimal> aplicarCupom(@RequestBody AplicarCupomRequest request) {
        BigDecimal valorFinal = aplicarCupomUseCase.aplicar(
                request.codigo(),
                request.valorPedido(),
                request.valorFrete()
        );
        return ResponseEntity.ok(valorFinal);
    }
}