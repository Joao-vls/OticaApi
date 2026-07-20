package br.com.otica.otica_loja.Entity.FAQ;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "faq_itens", schema = "loja")
public class FaqItem {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private FaqCategoria categoria;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pergunta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String resposta;

    @Column(nullable = false)
    private Long visualizacoes = 0L;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

}