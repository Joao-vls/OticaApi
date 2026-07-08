package br.com.otica.otica_loja.controller.admin;


import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Entity.CMS.VitrineProduto;
import br.com.otica.otica_loja.UseCases.cms.AdicionarProdutoVitrineUseCase;
import br.com.otica.otica_loja.UseCases.cms.CriarVitrineUseCase;
import br.com.otica.otica_loja.dto.cms.VincularProdutoRequestDTO;
import br.com.otica.otica_loja.dto.cms.VitrineAdminRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/vitrines")
@CrossOrigin(origins = "*") // Lembre-se de configurar a segurança/CORS do Admin conforme seu projeto
public class AdminVitrineController {


    // Dica: Assim que você criar os UseCases de Atualizar, Deletar e Listar, injete-os aqui de forma semelhante.
    private final CriarVitrineUseCase criarVitrineUseCase;
    private final AdicionarProdutoVitrineUseCase adicionarProdutoVitrineUseCase;

    public AdminVitrineController(
            CriarVitrineUseCase criarVitrineUseCase,
            AdicionarProdutoVitrineUseCase adicionarProdutoVitrineUseCase
    ) {
        this.criarVitrineUseCase = criarVitrineUseCase;
        this.adicionarProdutoVitrineUseCase = adicionarProdutoVitrineUseCase;
    }

    @GetMapping
    public ResponseEntity<String> listarVitrines() {
        // TODO: Implementar caso de uso de listagem para a tabela do painel administrativo
        return ResponseEntity.ok("lista de vitrines");
    }

    @PostMapping
    public ResponseEntity<?> criarVitrine(@RequestBody @Valid VitrineAdminRequestDTO dto) {
        try {
            // Converte o DTO recebido para o Command record esperado pelo UseCase
            CriarVitrineUseCase.Command command = new CriarVitrineUseCase.Command(
                    dto.nome(),
                    dto.slug(),
                    dto.titulo(),
                    dto.subtitulo(),
                    dto.ordem(),
                    dto.ativo()
            );

            Vitrine novaVitrine = criarVitrineUseCase.executar(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaVitrine);

        } catch (IllegalArgumentException e) {
            // Trata o erro caso o slug já exista (regra de negócio que você colocou no UseCase)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarVitrine(@PathVariable UUID id, @RequestBody @Valid VitrineAdminRequestDTO dto) {
        // Corrigido para UUID
        // TODO: Criar e invocar o AtualizarVitrineUseCase
        return ResponseEntity.ok("vitrine atualizada (id=" + id + ")");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVitrine(@PathVariable UUID id) {
        // Corrigido para UUID
        // TODO: Criar e invocar o DeletarVitrineUseCase
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/produtos")
    public ResponseEntity<?> vincularProduto(
            @PathVariable UUID id,
            @RequestBody @Valid VincularProdutoRequestDTO dto
    ) {
        try {
            VitrineProduto vinculo = adicionarProdutoVitrineUseCase.adicionarProduto(
                    id,
                    dto.produtoId(),
                    dto.ordem()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(vinculo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(e.getMessage());
        }
    }
}