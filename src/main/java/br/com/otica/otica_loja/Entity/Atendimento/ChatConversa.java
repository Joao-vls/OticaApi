package br.com.otica.otica_loja.Entity.Atendimento;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "chat_conversas", schema = "loja")
public class ChatConversa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId; // FK para auth.users

    @Column(name = "sessao_anonima", length = 255)
    private String sessaoAnonima;

    @Column(nullable = false, length = 50)
    private String canal = "site";
    // site, app, whatsapp, etc.

    @Column(nullable = false, length = 20)
    private String status = "aberto";
    // aberto, em_atendimento, encerrado

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    // Relacionamento com mensagens
    @OneToMany(mappedBy = "conversa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMensagem> mensagens;

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getSessaoAnonima() {
        return sessaoAnonima;
    }

    public void setSessaoAnonima(String sessaoAnonima) {
        this.sessaoAnonima = sessaoAnonima;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(OffsetDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public List<ChatMensagem> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<ChatMensagem> mensagens) {
        this.mensagens = mensagens;
    }
}