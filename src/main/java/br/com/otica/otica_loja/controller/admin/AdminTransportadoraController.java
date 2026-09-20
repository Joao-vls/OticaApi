package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Logistica.Transportadora;
import br.com.otica.otica_loja.UseCases.logistica.AlternarStatusTransportadoraUseCase;
import br.com.otica.otica_loja.UseCases.logistica.GerenciarTransportadoraUseCase;
import br.com.otica.otica_loja.UseCases.logistica.ListarTransportadorasUseCase;
import br.com.otica.otica_loja.dto.TransportadoraRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/logistica/transportadoras")
@RequiredArgsConstructor
public class AdminTransportadoraController {

    private final GerenciarTransportadoraUseCase gerenciarTransportadoraUseCase;
    private final ListarTransportadorasUseCase listarTransportadorasUseCase;
    private final AlternarStatusTransportadoraUseCase alternarStatusTransportadoraUseCase;

    // 1. Criar nova Transportadora (Apenas Admin/Gerente)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Transportadora> criar(@Valid @RequestBody TransportadoraRequest request) {
        Transportadora transportadora = gerenciarTransportadoraUseCase.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transportadora);
    }

    // 2. Editar Transportadora existente (Apenas Admin/Gerente)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Transportadora> atualizar(@PathVariable UUID id, @Valid @RequestBody TransportadoraRequest request) {
        Transportadora transportadora = gerenciarTransportadoraUseCase.atualizar(id, request);
        return ResponseEntity.ok(transportadora);
    }

    // 3. Ativar/Desativar Transportadora (Apenas Admin/Gerente)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Transportadora> alternarStatus(@PathVariable UUID id) {
        Transportadora transportadora = alternarStatusTransportadoraUseCase.alternarStatus(id);
        return ResponseEntity.ok(transportadora);
    }

    // 4. Listar TODAS as transportadoras (Para a tela de gerenciar no Admin)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<Transportadora>> listarTodas() {
        return ResponseEntity.ok(listarTransportadorasUseCase.listarTodas());
    }

    // 5. Listar apenas transportadoras ATIVAS (Utilizado para o Select no formulário de Despachar Pedido)
    // Liberado para ATENDENTE também, pois ele precisa escolher a transportadora ao despachar.
    @GetMapping("/ativas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<List<Transportadora>> listarAtivas() {
        return ResponseEntity.ok(listarTransportadorasUseCase.listarAtivas());
    }
}