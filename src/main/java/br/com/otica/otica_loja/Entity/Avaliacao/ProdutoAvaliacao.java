package br.com.otica.otica_loja.Entity.Avaliacao;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "produto_avaliacoes", schema = "loja",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_avaliacao_usuario_produto", columnNames = {"usuario_id", "produto_id"})
        })
public class ProdutoAvaliacao {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "usuario_id")
    private UUID usuarioId; // FK para auth.users

    @Column(name = "nome_usuario", nullable = false, length = 150)
    private String nomeUsuario;

    @Column(nullable = false)
    private Integer nota; // 1 a 5

    @Column(length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;

    @Column(name = "imagem_path", columnDefinition = "TEXT")
    private String imagemPath;

    @Column(nullable = false)
    private Boolean aprovado = false;

    @Column(name = "data_avaliacao", nullable = false)
    private LocalDate dataAvaliacao = LocalDate.now();

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}
