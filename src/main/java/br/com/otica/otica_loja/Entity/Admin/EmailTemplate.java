package br.com.otica.otica_loja.Entity.Admin;

import br.com.otica.otica_loja.enums.TipoEmail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "email_templates", schema = "admin")
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private TipoEmail tipo;

    @Column(nullable = false, length = 200)
    private String assunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String htmlBase;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = OffsetDateTime.now();
    }
}