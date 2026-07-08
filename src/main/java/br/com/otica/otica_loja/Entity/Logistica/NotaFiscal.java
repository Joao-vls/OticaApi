package br.com.otica.otica_loja.Entity.Logistica;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notas_fiscais", schema = "loja")
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Column(name = "numero_nfe", length = 100)
    private String numeroNfe;

    @Column(name = "chave_acesso", length = 255)
    private String chaveAcesso;

    @Column(name = "xml_path", columnDefinition = "TEXT")
    private String xmlPath;

    @Column(name = "pdf_path", columnDefinition = "TEXT")
    private String pdfPath;

    @Column(name = "emitida_em")
    private OffsetDateTime emitidaEm;

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getNumeroNfe() {
        return numeroNfe;
    }

    public void setNumeroNfe(String numeroNfe) {
        this.numeroNfe = numeroNfe;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public String getXmlPath() {
        return xmlPath;
    }

    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public OffsetDateTime getEmitidaEm() {
        return emitidaEm;
    }

    public void setEmitidaEm(OffsetDateTime emitidaEm) {
        this.emitidaEm = emitidaEm;
    }
}
