package br.com.otica.otica_loja.Entity.Logistica;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "transportadoras", schema = "loja")
public class Transportadora {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 100, unique = true)
    private String codigo;

    @Column(nullable = false)
    private Boolean ativo = true;

}