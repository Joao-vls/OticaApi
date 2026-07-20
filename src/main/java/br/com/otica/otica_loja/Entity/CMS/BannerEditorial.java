package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "banners_editoriais", schema = "loja")
public class BannerEditorial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 150)
    private String identificador; // Ex: "HOME_PROMO_MAIN", "HOME_PROMO_SIDE"

    @Column(name = "layout_tipo", nullable = false, length = 50)
    private String layoutTipo; // "VERTICAL" (Caso 1) ou "HORIZONTAL" (Caso 2)

    @Column(name = "texto_marca", length = 150)
    private String textoMarca; // Nome da Marca principal/Logo texto

    @Column(name = "logo_path", columnDefinition = "TEXT")
    private String logoPath;

    // Getters/Setters Seção 1
    // ==========================================
    // SEÇÃO 1 (Representa: Topo ou Esquerda)
    // ==========================================
    @Column(name = "sec1_media_path", nullable = false, columnDefinition = "TEXT")
    private String sec1MediaPath;

    @Column(name = "sec1_titulo")
    private String sec1Titulo;

    @Column(name = "sec1_titulo_destaque")
    private String sec1TituloDestaque; // Palavra vermelha/destacada no título

    @Column(name = "sec1_descricao", columnDefinition = "TEXT")
    private String sec1Descricao;

    @Column(name = "sec1_produto_nome")
    private String sec1ProdutoNome;

    @Column(name = "sec1_preco", precision = 10, scale = 2)
    private BigDecimal sec1Preco;

    @Column(name = "sec1_desconto")
    private Integer sec1Desconto; // Ex: 20 (para 20%)

    @Column(name = "sec1_link_url", columnDefinition = "TEXT")
    private String sec1LinkUrl; // Destino do botão "Saiba mais"

    // Getters/Setters Seção 2
    // ==========================================
    // SEÇÃO 2 (Representa: Fundo ou Direita)
    // ==========================================
    @Column(name = "sec2_media_path", nullable = false, columnDefinition = "TEXT")
    private String sec2MediaPath;

    @Column(name = "sec2_titulo")
    private String sec2Titulo;

    @Column(name = "sec2_titulo_destaque")
    private String sec2TituloDestaque;

    @Column(name = "sec2_descricao", columnDefinition = "TEXT")
    private String sec2Descricao;

    @Column(name = "sec2_produto_nome")
    private String sec2ProdutoNome;

    @Column(name = "sec2_preco", precision = 10, scale = 2)
    private BigDecimal sec2Preco;

    @Column(name = "sec2_desconto")
    private Integer sec2Desconto; // Ex: 40 (para 40%)

    @Column(name = "sec2_link_url", columnDefinition = "TEXT")
    private String sec2LinkUrl;

    // ==========================================
    // METADADOS
    // ==========================================
    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    // ==========================================
    // GETTERS E SETTERS
    // ==========================================

}