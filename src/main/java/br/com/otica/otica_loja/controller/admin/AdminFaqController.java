package br.com.otica.otica_loja.controller.admin;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/faqs")
public class AdminFaqController {

    @GetMapping
    public String listarFaqs() {
        return "lista de FAQs";
    }

    @PostMapping
    public String criarFaq(@RequestBody String faq) {
        return "FAQ criada: " + faq;
    }

    @PutMapping("/{id}")
    public String atualizarFaq(@PathVariable Long id, @RequestBody String faq) {
        return "FAQ atualizada (id=" + id + "): " + faq;
    }

    @DeleteMapping("/{id}")
    public String deletarFaq(@PathVariable Long id) {
        return "FAQ deletada (id=" + id + ")";
    }
}
