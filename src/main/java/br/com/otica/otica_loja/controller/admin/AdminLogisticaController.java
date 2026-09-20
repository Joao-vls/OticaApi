package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Logistica.Envio;
import br.com.otica.otica_loja.Entity.Logistica.EnvioEvento;
import br.com.otica.otica_loja.UseCases.logistica.*;
import br.com.otica.otica_loja.dto.AdicionarEventoRequest;
import br.com.otica.otica_loja.dto.ConfirmarEntregaRequest;
import br.com.otica.otica_loja.dto.CriarEnvioRequest;
import br.com.otica.otica_loja.dto.EnvioResumoDTO;
import br.com.otica.otica_loja.enums.StatusPedido;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/logistica")
@RequiredArgsConstructor
public class AdminLogisticaController {

    private final CriarEnvioUseCase criarEnvioUseCase;
    private final ConfirmarEntregaUseCase confirmarEntregaUseCase;
    private final AtualizarRastreamentoUseCase atualizarRastreamentoUseCase;
    private final BuscarRastreamentoUseCase buscarRastreamentoUseCase;
    private final ListarEnviosAdminUseCase listarEnviosAdminUseCase;
    // 1. Criar um novo envio (Despachar pedido)
    @PostMapping("/envios")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<Envio> criarEnvio(@Valid @RequestBody CriarEnvioRequest request) {

        Envio envio = criarEnvioUseCase.criarEnvio(
                request.pedidoId(),
                request.transportadoraId(),
                request.codigoRastreio(),
                request.urlRastreio(),
                request.valorFrete(),
                request.observacao()
        );

        return ResponseEntity.ok(envio);
    }

    // 2. Confirmar a entrega de um envio ao cliente
    @PostMapping("/envios/{envioId}/entregar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<Envio> confirmarEntrega(
            @PathVariable UUID envioId,
            @Valid @RequestBody ConfirmarEntregaRequest request) {

        Envio envio = confirmarEntregaUseCase.confirmarEntrega(
                envioId,
                request.localizacao(),
                request.observacao()
        );

        return ResponseEntity.ok(envio);
    }

    // 3. Adicionar um evento/atualização de rastreamento no envio
    @PostMapping("/envios/{envioId}/eventos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<EnvioEvento> adicionarEventoRastreamento(
            @PathVariable UUID envioId,
            @Valid @RequestBody AdicionarEventoRequest request) {

        EnvioEvento evento = atualizarRastreamentoUseCase.adicionarEvento(
                envioId,
                request.descricao(),
                request.localizacao()
        );

        return ResponseEntity.ok(evento);
    }

    // 4. Listar todo o histórico de rastreamento de um envio
    @GetMapping("/envios/{envioId}/eventos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<List<EnvioEvento>> buscarEventosRastreamento(@PathVariable UUID envioId) {

        List<EnvioEvento> eventos = buscarRastreamentoUseCase.buscarEventos(envioId);
        return ResponseEntity.ok(eventos);

    }
    // 5. Listar todos os envios com dados resumidos do cliente
    @GetMapping("/envios/todos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<List<EnvioResumoDTO>> listarTodosEnvios() {
        List<EnvioResumoDTO> lista = listarEnviosAdminUseCase.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // 6. Listar envios filtrando pelo status do pedido (ex: ENVIADO, ENTREGUE, SEPARACAO)
    @GetMapping("/envios/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<List<EnvioResumoDTO>> listarEnviosPorStatus(@PathVariable StatusPedido status) {
        List<EnvioResumoDTO> lista = listarEnviosAdminUseCase.listarPorStatus(status);
        return ResponseEntity.ok(lista);
    }
}