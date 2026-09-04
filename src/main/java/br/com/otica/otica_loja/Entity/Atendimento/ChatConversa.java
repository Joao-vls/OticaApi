package br.com.otica.otica_loja.Entity.Atendimento;

import com.fasterxml.jackson.annotation.JsonIgnore; // Import necessário
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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "sessao_anonima")
    private String sessaoAnonima;

    @Column(nullable = false, length = 50)
    private String canal = "site";

    @Column(nullable = false, length = 20)
    private String status = "aberto";

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    // Ignora o carregamento automático da lista para otimizar requisições
    @JsonIgnore
    @OneToMany(mappedBy = "conversa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMensagem> mensagens;
}