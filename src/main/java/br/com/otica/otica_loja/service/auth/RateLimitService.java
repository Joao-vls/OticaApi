package br.com.otica.otica_loja.service.auth;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    // Armazena os limites em memória baseados no IP
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    // Resolve o limite de Login
    public Bucket resolveLoginBucket(String ip) {
        return loginBuckets.computeIfAbsent(ip, this::newLoginBucket);
    }

    // Resolve o limite de Registro
    public Bucket resolveRegisterBucket(String ip) {
        return registerBuckets.computeIfAbsent(ip, this::newRegisterBucket);
    }

    // Configuração para Login: 5 tentativas por minuto
    private Bucket newLoginBucket(String ip) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillGreedy(5, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    // Configuração para Registro: 3 registros por hora
    private Bucket newRegisterBucket(String ip) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(3)
                .refillGreedy(3, Duration.ofHours(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    // Método utilitário para pegar o IP real caso você use Nginx/Cloudflare
    public String obterIpCliente(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}