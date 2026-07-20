package br.com.otica.otica_loja.Entity.Logistica;

import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "notas_fiscais", schema = "loja")
public class NotaFiscal {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Column(name = "numero_nfe", length = 100)
    private String numeroNfe;

    @Column(name = "chave_acesso")
    private String chaveAcesso;

    @Column(name = "xml_path", columnDefinition = "TEXT")
    private String xmlPath;

    @Column(name = "pdf_path", columnDefinition = "TEXT")
    private String pdfPath;

    @Column(name = "emitida_em")
    private OffsetDateTime emitidaEm;

}
