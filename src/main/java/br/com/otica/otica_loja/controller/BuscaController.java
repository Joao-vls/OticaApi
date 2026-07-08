package br.com.otica.otica_loja.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/busca")
public class BuscaController {

    @GetMapping
    public String busca(@RequestParam(name = "q", required = false) String query) {
        return "busca: " + query;
    }

    @GetMapping("/sugestoes")
    public String sugestoes() {
        return "sugestoes";
    }
}
