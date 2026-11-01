package br.com.otica.otica_loja.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @GetMapping("/health")
    public ResponseEntity<String> checkHealth() {
        try {
            // 1. Executa uma consulta leve para acordar/manter o PostgreSQL ativo
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            // 2. Envia um comando PING leve para acordar/manter o Redis ativo
            // Usamos o try-with-resources para garantir que a conexão será fechada após o ping
            try (RedisConnection connection = redisConnectionFactory.getConnection()) {
                connection.ping();
            }

            return ResponseEntity.ok("API, Banco de Dados e Redis estão online e ativos!");

        } catch (Exception e) {
            // Retorna 500 se o banco de dados ou o Redis falharem ao responder
            return ResponseEntity.status(500).body("Erro de conexão (PostgreSQL ou Redis): " + e.getMessage());
        }
    }
}