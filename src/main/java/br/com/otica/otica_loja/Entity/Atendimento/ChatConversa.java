package br.com.otica.otica_loja.Entity.Atendimento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "chat_conversas", schema = "loja")
public class ChatConversa {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId; // FK para auth.users

    @Column(name = "sessao_anonima")
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

}