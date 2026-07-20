package br.com.otica.otica_loja.Entity.Auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.*;

@Setter
@Getter
@Entity
@Table(name = "usuarios", schema = "loja")
public class Usuario {

    // Getters e Setters
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false)
    private String senhaHash;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "verificado", nullable = false)
    private Boolean verificado = false;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Perfil perfil;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_permissoes",
            schema = "loja",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private Set<Permissao> permissoes = new HashSet<>();

    // Construtores
    public Usuario() {}

    public Usuario(Perfil perfil) {
        this.perfil = perfil;
        perfil.setUsuario(this);
    }



    public void addPermissao(Permissao permissao) {
        this.permissoes.add(permissao);
    }
}
