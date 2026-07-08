package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Estoque.EstoqueMovimentacao;
import br.com.otica.otica_loja.UseCases.estoque.*;
import br.com.otica.otica_loja.dto.AjusteEstoqueRequest;
import br.com.otica.otica_loja.dto.MovimentacaoEstoqueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/estoque")
public class AdminEstoqueController {

    @Autowired
    private ConsultarEstoqueUseCase consultarEstoqueUseCase;

    @Autowired
    private EntradaEstoqueUseCase entradaEstoqueUseCase;

    @Autowired
    private ReservarEstoqueUseCase reservarEstoqueUseCase;

    @Autowired
    private SaidaEstoqueUseCase saídasEstoqueUseCase;

    @Autowired
    private LiberarEstoqueUseCase liberarEstoqueUseCase;

    @Autowired
    private AjusteEstoqueUseCase ajusteEstoqueUseCase;

    // 1. Consulta o saldo atual de uma variante específica
    @GetMapping("/variante/{varianteId}/saldo")
    public ResponseEntity<Integer> consultarSaldo(@PathVariable UUID varianteId) {
        Integer saldo = consultarEstoqueUseCase.consultarSaldo(varianteId);
        return ResponseEntity.ok(saldo);
    }

    // 2. Consulta o histórico completo de movimentações de uma variante
    @GetMapping("/variante/{varianteId}/historico")
    public ResponseEntity<List<EstoqueMovimentacao>> consultarHistorico(@PathVariable UUID varianteId) {
        List<EstoqueMovimentacao> historico = consultarEstoqueUseCase.consultarHistorico(varianteId);
        return ResponseEntity.ok(historico);
    }

    // 3. Entrada de produto no estoque (Soma ao estoque atual)
    @PostMapping("/entrada")
    public ResponseEntity<EstoqueMovimentacao> entradaEstoque(@RequestBody MovimentacaoEstoqueRequest request) {
        EstoqueMovimentacao movimentacao = entradaEstoqueUseCase.registrarEntrada(
                request.varianteId(),
                request.quantidade(),
                request.usuarioId(),
                request.observacao()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }

    // 4. Saída/Reserva de produto no estoque (Subtrai do estoque atual e valida saldo)
    @PostMapping("/saida")
    public ResponseEntity<EstoqueMovimentacao> saidaEstoque(@RequestBody MovimentacaoEstoqueRequest request) {
        EstoqueMovimentacao movimentacao = reservarEstoqueUseCase.reservar(
                request.varianteId(),
                request.quantidade(),
                request.usuarioId(),
                request.observacao()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }

    // 5. Liberação de estoque (Devolve ao estoque itens cancelados/estornados)
    @PostMapping("/liberar")
    public ResponseEntity<EstoqueMovimentacao> liberarEstoque(@RequestBody MovimentacaoEstoqueRequest request) {
        EstoqueMovimentacao movimentacao = liberarEstoqueUseCase.liberar(
                request.varianteId(),
                request.quantidade(),
                request.usuarioId(),
                request.observacao()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }

    // 6. Ajuste manual de estoque (Define diretamente o valor final do saldo e calcula a diferença)
    @PostMapping("/ajuste")
    public ResponseEntity<EstoqueMovimentacao> ajusteEstoque(@RequestBody AjusteEstoqueRequest request) {
        EstoqueMovimentacao movimentacao = ajusteEstoqueUseCase.registrarAjuste(
                request.varianteId(),
                request.novoSaldo(),
                request.usuarioId(),
                request.observacao()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }

    // 4.1. Reserva de produto para carrinho/pedido (Usa o ReservarEstoqueUseCase -> TipoMovimentacao.VENDA)
    @PostMapping("/reservar")
    public ResponseEntity<EstoqueMovimentacao> reservarEstoque(@RequestBody MovimentacaoEstoqueRequest request) {
        EstoqueMovimentacao movimentacao = reservarEstoqueUseCase.reservar(
                request.varianteId(),
                request.quantidade(),
                request.usuarioId(),
                request.observacao()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }
}