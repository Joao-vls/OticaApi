package br.com.otica.otica_loja.service.admin;


import br.com.otica.otica_loja.Entity.Admin.LogAcesso;
import br.com.otica.otica_loja.Repository.Admin.LogAcessoRepository; // Crie este repository se não tiver
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class LogAcessoService {

    @Autowired
    private LogAcessoRepository logAcessoRepository;

    public void registrar(HttpServletRequest request, UUID usuarioId) {
        try {
            LogAcesso log = new LogAcesso();
            log.setUsuarioId(usuarioId); // Pode ser null se a tentativa falhar
            log.setMetodo(request.getMethod());
            log.setRota(request.getRequestURI());
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setIp(obterIpCliente(request));
            log.setCriadoEm(OffsetDateTime.now());

            logAcessoRepository.save(log);
        } catch (Exception e) {
            // Logamos o erro internamente para que uma falha ao salvar o log não derrube o fluxo de login do usuário
            System.err.println("Falha ao salvar log de acesso: " + e.getMessage());
        }
    }

    /**
     * Captura o IP real do cliente, mesmo se a aplicação estiver atrás de um Proxy ou Load Balancer (como Cloudflare, Nginx)
     */
    private String obterIpCliente(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Se houver múltiplos IPs no Header (cadeia de proxies), pega o primeiro (IP original do cliente)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}