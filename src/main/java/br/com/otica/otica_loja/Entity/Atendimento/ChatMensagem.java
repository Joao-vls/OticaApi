package br.com.otica.otica_loja.Entity.Atendimento;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import br.com.otica.otica_loja.enums.RemetenteTipo;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "chat_mensagens", schema = "loja")
public class ChatMensagem {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversa_id", nullable = false)
    private ChatConversa conversa;

    @Enumerated(EnumType.STRING) // importante para salvar como texto no banco
    @Column(nullable = false, length = 20)
    private RemetenteTipo remetente;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "arquivo_path", columnDefinition = "TEXT")
    private String arquivoPath;

    @Column(nullable = false)
    private Boolean visualizada = false;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}
