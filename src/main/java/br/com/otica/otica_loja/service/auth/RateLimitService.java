package br.com.otica.otica_loja.service.auth;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    // Armazena os limites em memória baseados no IP
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> newsletterBuckets = new ConcurrentHashMap<>();

    // 🔥 Mapa para guardar as tentativas de reenvio em memória (Bloqueio Exponencial)
    private final ConcurrentHashMap<String, ResendAttempt> resendAttempts = new ConcurrentHashMap<>();

    private static class ResendAttempt {
        int tentativas = 0;
        OffsetDateTime ultimaTentativa = OffsetDateTime.now();
    }

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

    /**
     * 🔥 Calcula o tempo de espera dobrando a cada tentativa (30s, 60s, 120s...)
     * @return 0 se puder enviar, ou o tempo restante em segundos se estiver bloqueado.
     */
    public long verificarRateLimitExponencial(String chaveUnica) {
        OffsetDateTime agora = OffsetDateTime.now();
        ResendAttempt attempt = resendAttempts.computeIfAbsent(chaveUnica, k -> new ResendAttempt());

        synchronized (attempt) {
            // Primeira tentativa
            if (attempt.tentativas == 0) {
                attempt.tentativas = 1;
                attempt.ultimaTentativa = agora;
                return 0;
            }

            // Calcula a espera: 30s * 2^(tentativas - 1)
            long waitSeconds = 30L * (long) Math.pow(2, attempt.tentativas - 1);
            OffsetDateTime limiteParaProximoEnvio = attempt.ultimaTentativa.plusSeconds(waitSeconds);

            // Verifica se ainda está no tempo de bloqueio
            if (agora.isBefore(limiteParaProximoEnvio)) {
                return Duration.between(agora, limiteParaProximoEnvio).getSeconds(); // Retorna os segundos faltantes
            }

            // Se já passou o tempo exigido, autoriza o envio.
            // Se passou muito tempo desde o último envio (ex: 2 horas), "reseta" o multiplicador.
            if (Duration.between(attempt.ultimaTentativa, agora).toHours() >= 2) {
                attempt.tentativas = 1;
            } else {
                attempt.tentativas++; // Aumenta o multiplicador para a próxima tentativa
            }

            attempt.ultimaTentativa = agora;
            return 0; // Pode enviar
        }
    }
    public Bucket resolveNewsletterBucket(String ip) {
        return newsletterBuckets.computeIfAbsent(ip, this::newNewsletterBucket);
    }

    // Configuração para Newsletter: Máximo de 2 inscrições por semana por IP
    private Bucket newNewsletterBucket(String ip) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(2) // Máximo de 2
                .refillGreedy(2, Duration.ofDays(7)) // Recarrega 2 a cada 7 dias
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}