package br.com.otica.otica_loja.Entity.CMS;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "social_showcase", schema = "loja")
public class SocialShowcase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(name = "marca_nome", nullable = false, length = 150)
    private String marcaNome;

    @Column(name = "modelo_nome", nullable = false, length = 150)
    private String modeloNome;

    @Column(length = 150)
    private String username;

    @Column(name = "thumbnail_path", columnDefinition = "TEXT")
    private String thumbnailPath;

    @Column(name = "video_path", nullable = false, columnDefinition = "TEXT")
    private String videoPath;

    @Column(nullable = false)
    private Long visualizacoes = 0L;

    @Column(nullable = false)
    private Long curtidas = 0L;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}