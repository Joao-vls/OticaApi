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
import java.util.List;
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

    @Autowired
    private ListarCuponsUseCase listarCuponsUseCase;

    @PostMapping
    public ResponseEntity<Cupom> criarCupom(@RequestBody CriarCupomRequest request) {
        Cupom novoCupom = criarCupomUseCase.criar(
                request.codigo(),
                request.descricao(),
                request.tipo(),
                request.valor(),
                request.valorMinimoPedido(),
                request.quantidadeTotal(),
                request.limiteItensPorPedido(),
                request.dataInicio(),
                request.dataFim(),
                request.usuariosIdsEspecificos(),
                request.produtosIdsEspecificos(),
                request.categoriasIdsEspecificas(),
                request.usoUnico()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCupom);
    }

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
                request.limiteItensPorPedido(),
                request.dataInicio(),
                request.dataFim(),
                request.ativo(),
                request.usuariosIdsEspecificos(),
                request.produtosIdsEspecificos(),
                request.categoriasIdsEspecificas(),
                request.usoUnico()
        );
        return ResponseEntity.ok(cupomAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCupom(@PathVariable UUID id) {
        excluirCupomUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Cupom>> listarTodos() {
        List<Cupom> cupons = listarCuponsUseCase.listarTodos();
        return ResponseEntity.ok(cupons);
    }

    @GetMapping("/validar")
    public ResponseEntity<Cupom> validarCupom(@RequestParam String codigo, @RequestParam BigDecimal valorPedido) {
        Cupom cupom = validarCupomUseCase.validar(codigo, valorPedido);
        return ResponseEntity.ok(cupom);
    }

    @PostMapping("/aplicar")
    public ResponseEntity<BigDecimal> aplicarCupom(@RequestBody AplicarCupomRequest request) {
        BigDecimal valorFinal = aplicarCupomUseCase.aplicar(
                request.codigo(),
                request.valorPedido(),
                request.valorFrete(),
                request.usuarioId(),
                request.produtosIds()
        );
        return ResponseEntity.ok(valorFinal);
    }
}