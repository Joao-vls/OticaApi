package br.com.otica.otica_loja.Entity.CMS;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet; // 🔥 Mantém a ordenação definida pelo @OrderBy
import java.util.Set; // 🔥 Evita problemas de múltiplas listas (Bags) no Hibernate
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "vitrines", schema = "loja")
public class Vitrine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String subtitulo;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    // 🔥 Otimizado para Set + LinkedHashSet para evitar Cartesian Product
    // 🔥 Adicionado @OrderBy para garantir que os produtos venham na ordem correta do painel/banco
    @OneToMany(mappedBy = "vitrine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("ordem ASC")
    @JsonIgnoreProperties("vitrine")
    private Set<VitrineProduto> produtos = new LinkedHashSet<>();

}