package br.com.otica.otica_loja.Entity.Catalogo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "produto_tags", schema = "loja")
public class ProdutoTag {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    // Relacionamento ManyToMany com Produto via tabela de junção
    @ManyToMany(mappedBy = "tags")
    private List<Produto> produtos;

}
