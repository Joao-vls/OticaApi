package br.com.otica.otica_loja.Entity.Catalogo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "marcas", schema = "loja")
public class Marca {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "logo_path", columnDefinition = "TEXT")
    private String logoPath;

    @Column(name = "banner_path", columnDefinition = "TEXT")
    private String bannerPath;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    @Column(name = "deletado_em")
    private OffsetDateTime deletadoEm;

}
