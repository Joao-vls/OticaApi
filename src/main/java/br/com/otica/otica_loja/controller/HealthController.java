package br.com.otica.otica_loja.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public ResponseEntity<String> checkHealth() {
        try {
            // Executa uma consulta extremamente leve para acordar/manter ativo o banco de dados
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return ResponseEntity.ok("API e Banco de Dados estão online e ativos!");
        } catch (Exception e) {
            // Retorna 500 se o banco falhar, alertando que há problemas
            return ResponseEntity.status(500).body("Erro de conexão com o banco: " + e.getMessage());
        }
    }
}