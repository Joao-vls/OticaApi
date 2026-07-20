package br.com.otica.otica_loja.Entity.ReceitaMedica;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "receitas_medicas", schema = "loja")
public class ReceitaMedica {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId; // FK para auth.users

    @Column(name = "nome_arquivo", nullable = false)
    private String nomeArquivo;

    @Column(name = "arquivo_path", nullable = false, columnDefinition = "TEXT")
    private String arquivoPath;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}
