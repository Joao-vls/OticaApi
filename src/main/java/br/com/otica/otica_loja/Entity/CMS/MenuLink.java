package br.com.otica.otica_loja.Entity.CMS;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "menu_links", schema = "loja")
public class MenuLink {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private MenuCategoria categoria;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 500)
    private String url;

    @Column(name = "target_blank", nullable = false)
    private Boolean targetBlank = false;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(nullable = false)
    private Boolean ativo = true;

}
