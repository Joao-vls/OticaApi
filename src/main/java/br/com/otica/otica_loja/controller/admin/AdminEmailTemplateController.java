package br.com.otica.otica_loja.controller.admin;

import br.com.otica.otica_loja.Entity.Admin.EmailTemplate;
import br.com.otica.otica_loja.Repository.Admin.EmailTemplateRepository;
import br.com.otica.otica_loja.dto.AtualizarEmailTemplateDTO;
import br.com.otica.otica_loja.enums.TipoEmail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/email-templates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEmailTemplateController {

    private final EmailTemplateRepository templateRepository;

    @GetMapping
    public ResponseEntity<List<EmailTemplate>> listarTodos() {
        return ResponseEntity.ok(templateRepository.findAll());
    }

    @GetMapping("/{tipo}")
    public ResponseEntity<?> buscarPorTipo(@PathVariable TipoEmail tipo) {
        return templateRepository.findByTipo(tipo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<?> salvarOuAtualizar(@Valid @RequestBody AtualizarEmailTemplateDTO dto) {
        EmailTemplate template = templateRepository.findByTipo(dto.tipo())
                .orElseGet(() -> {
                    EmailTemplate novo = new EmailTemplate();
                    novo.setTipo(dto.tipo());
                    return novo;
                });

        template.setAssunto(dto.assunto());
        template.setHtmlBase(dto.htmlBase());
        if (dto.ativo() != null) {
            template.setAtivo(dto.ativo());
        }

        templateRepository.save(template);
        return ResponseEntity.ok(Map.of("message", "Template de e-mail atualizado com sucesso."));
    }
}