package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "paginas_blocos", schema = "loja")
public class PaginaBloco {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pagina_id", nullable = false)
    private PaginaCMS pagina;

    @Column(nullable = false, length = 100)
    private String tipo;

    @Column(name = "configuracao", nullable = false, columnDefinition = "JSONB")
    private String configuracao = "{}";

    @Column(nullable = false)
    private Integer ordem = 0;

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PaginaCMS getPagina() {
        return pagina;
    }

    public void setPagina(PaginaCMS pagina) {
        this.pagina = pagina;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(String configuracao) {
        this.configuracao = configuracao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}
