package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

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

    // ==========================================
    // SEÇÃO 1 (Representa: Topo ou Esquerda)
    // ==========================================
    @Column(name = "sec1_media_path", nullable = false, columnDefinition = "TEXT")
    private String sec1MediaPath;

    @Column(name = "sec1_titulo", length = 255)
    private String sec1Titulo;

    @Column(name = "sec1_titulo_destaque", length = 255)
    private String sec1TituloDestaque; // Palavra vermelha/destacada no título

    @Column(name = "sec1_descricao", columnDefinition = "TEXT")
    private String sec1Descricao;

    @Column(name = "sec1_produto_nome", length = 255)
    private String sec1ProdutoNome;

    @Column(name = "sec1_preco", precision = 10, scale = 2)
    private BigDecimal sec1Preco;

    @Column(name = "sec1_desconto")
    private Integer sec1Desconto; // Ex: 20 (para 20%)

    @Column(name = "sec1_link_url", columnDefinition = "TEXT")
    private String sec1LinkUrl; // Destino do botão "Saiba mais"

    // ==========================================
    // SEÇÃO 2 (Representa: Fundo ou Direita)
    // ==========================================
    @Column(name = "sec2_media_path", nullable = false, columnDefinition = "TEXT")
    private String sec2MediaPath;

    @Column(name = "sec2_titulo", length = 255)
    private String sec2Titulo;

    @Column(name = "sec2_titulo_destaque", length = 255)
    private String sec2TituloDestaque;

    @Column(name = "sec2_descricao", columnDefinition = "TEXT")
    private String sec2Descricao;

    @Column(name = "sec2_produto_nome", length = 255)
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

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }

    public String getLayoutTipo() { return layoutTipo; }
    public void setLayoutTipo(String layoutTipo) { this.layoutTipo = layoutTipo; }

    public String getTextoMarca() { return textoMarca; }
    public void setTextoMarca(String textoMarca) { this.textoMarca = textoMarca; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    // Getters/Setters Seção 1
    public String getSec1MediaPath() { return sec1MediaPath; }
    public void setSec1MediaPath(String sec1MediaPath) { this.sec1MediaPath = sec1MediaPath; }
    public String getSec1Titulo() { return sec1Titulo; }
    public void setSec1Titulo(String sec1Titulo) { this.sec1Titulo = sec1Titulo; }
    public String getSec1TituloDestaque() { return sec1TituloDestaque; }
    public void setSec1TituloDestaque(String sec1TituloDestaque) { this.sec1TituloDestaque = sec1TituloDestaque; }
    public String getSec1Descricao() { return sec1Descricao; }
    public void setSec1Descricao(String sec1Descricao) { this.sec1Descricao = sec1Descricao; }
    public String getSec1ProdutoNome() { return sec1ProdutoNome; }
    public void setSec1ProdutoNome(String sec1ProdutoNome) { this.sec1ProdutoNome = sec1ProdutoNome; }
    public BigDecimal getSec1Preco() { return sec1Preco; }
    public void setSec1Preco(BigDecimal sec1Preco) { this.sec1Preco = sec1Preco; }
    public Integer getSec1Desconto() { return sec1Desconto; }
    public void setSec1Desconto(Integer sec1Desconto) { this.sec1Desconto = sec1Desconto; }
    public String getSec1LinkUrl() { return sec1LinkUrl; }
    public void setSec1LinkUrl(String sec1LinkUrl) { this.sec1LinkUrl = sec1LinkUrl; }

    // Getters/Setters Seção 2
    public String getSec2MediaPath() { return sec2MediaPath; }
    public void setSec2MediaPath(String sec2MediaPath) { this.sec2MediaPath = sec2MediaPath; }
    public String getSec2Titulo() { return sec2Titulo; }
    public void setSec2Titulo(String sec2Titulo) { this.sec2Titulo = sec2Titulo; }
    public String getSec2TituloDestaque() { return sec2TituloDestaque; }
    public void setSec2TituloDestaque(String sec2TituloDestaque) { this.sec2TituloDestaque = sec2TituloDestaque; }
    public String getSec2Descricao() { return sec2Descricao; }
    public void setSec2Descricao(String sec2Descricao) { this.sec2Descricao = sec2Descricao; }
    public String getSec2ProdutoNome() { return sec2ProdutoNome; }
    public void setSec2ProdutoNome(String sec2ProdutoNome) { this.sec2ProdutoNome = sec2ProdutoNome; }
    public BigDecimal getSec2Preco() { return sec2Preco; }
    public void setSec2Preco(BigDecimal sec2Preco) { this.sec2Preco = sec2Preco; }
    public Integer getSec2Desconto() { return sec2Desconto; }
    public void setSec2Desconto(Integer sec2Desconto) { this.sec2Desconto = sec2Desconto; }
    public String getSec2LinkUrl() { return sec2LinkUrl; }
    public void setSec2LinkUrl(String sec2LinkUrl) { this.sec2LinkUrl = sec2LinkUrl; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime updated) { this.atualizadoEm = updated; }
}