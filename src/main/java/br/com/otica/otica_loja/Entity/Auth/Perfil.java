package br.com.otica.otica_loja.Entity.Auth;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "perfis", schema = "app")
public class Perfil {

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

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Boolean getEmailNotificacoes() {
        return emailNotificacoes;
    }

    public void setEmailNotificacoes(Boolean emailNotificacoes) {
        this.emailNotificacoes = emailNotificacoes;
    }

    public Boolean getWhatsappNotificacoes() {
        return whatsappNotificacoes;
    }

    public void setWhatsappNotificacoes(Boolean whatsappNotificacoes) {
        this.whatsappNotificacoes = whatsappNotificacoes;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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
}
