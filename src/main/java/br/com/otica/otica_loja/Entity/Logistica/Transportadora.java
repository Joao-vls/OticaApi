package br.com.otica.otica_loja.Entity.Logistica;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "transportadoras", schema = "loja")
public class Transportadora {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 100, unique = true)
    private String codigo;

    @Column(nullable = false)
    private Boolean ativo = true;

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}