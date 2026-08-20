package br.com.otica.otica_loja.Entity.Auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
@Entity
@Table(name = "usuarios", schema = "loja")
public class Usuario implements UserDetails {

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

    public Usuario() {}

    public Usuario(Perfil perfil) {
        this.perfil = perfil;
        perfil.setUsuario(this);
    }

    public void addPermissao(Permissao permissao) {
        this.permissoes.add(permissao);
    }

    // Métodos do UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (permissoes == null || permissoes.isEmpty()) {
            return Collections.emptySet();
        }

        return permissoes.stream()
                .map(p -> {
                    String nomePermissao = p.getNome().name().toUpperCase();
                    // Se o Enum/Nome já tiver "ROLE_", não adiciona novamente
                    if (!nomePermissao.startsWith("ROLE_")) {
                        nomePermissao = "ROLE_" + nomePermissao;
                    }
                    return new SimpleGrantedAuthority(nomePermissao);
                })
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return this.senhaHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return this.ativo; }
}