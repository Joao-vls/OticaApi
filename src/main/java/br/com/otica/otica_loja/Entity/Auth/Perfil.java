package br.com.otica.otica_loja.Entity.Auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "perfis", schema = "app")
public class Perfil {

    // Getters e Setters
    @Id
    private UUID id; // mesmo ID do usuário em auth.usuarios

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario; // ✅ relacionamento direto com Usuario

    @Column(unique = true, length = 50)
    private String username;

    @Column(length = 150)
    private String nome;

    @Column(length = 20)
    private String telefone;

    @Column(length = 20)
    private String genero; // masculino, feminino, outros

    @Column(name = "avatar_path", columnDefinition = "TEXT")
    private String avatarPath;

    @Column(length = 14)
    private String cpf;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "email_notificacoes", nullable = false)
    private Boolean emailNotificacoes = true;

    @Column(name = "whatsapp_notificacoes", nullable = false)
    private Boolean whatsappNotificacoes = true;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

}
