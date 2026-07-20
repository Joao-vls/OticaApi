package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "paginas_blocos", schema = "loja")
public class PaginaBloco {

    // Getters e Setters
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

}
