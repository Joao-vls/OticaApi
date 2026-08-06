package br.com.otica.otica_loja.Entity.Admin;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "logs_acesso",
        schema = "admin",
        indexes = {
                @Index(name = "idx_log_acesso_criado_em", columnList = "criado_em"),
                @Index(name = "idx_log_acesso_busca_grafico", columnList = "criado_em, rota")
        }
)
public class LogAcesso {

    // Define o fuso horário do Brasil
    private static final ZoneId ZONE_BRASIL = ZoneId.of("America/Sao_Paulo");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;

    @Column(name = "session_id", length = 100)
    private String sessionId; // Token de sessão/cookie para visitantes anônimos

    @Column(length = 100)
    private String ip;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String rota;

    @Column(length = 20)
    private String metodo;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        if (this.criadoEm == null) {
            this.criadoEm = OffsetDateTime.now(ZONE_BRASIL);
        }
    }

    public LogAcesso(UUID usuarioId, String sessionId, String ip, String userAgent, String rota, String metodo) {
        this.usuarioId = usuarioId;
        this.sessionId = sessionId;
        this.ip = ip;
        this.userAgent = userAgent;
        this.rota = rota;
        this.metodo = metodo;
        this.criadoEm = OffsetDateTime.now(ZONE_BRASIL);
    }
}