package br.com.otica.otica_loja.Entity.Auth;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import br.com.otica.otica_loja.enums.PermissaoNome;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "permissoes", schema = "loja")
public class Permissao {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 100)
    private PermissaoNome nome; // ADMIN, GERENTE, CLIENTE

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

}
