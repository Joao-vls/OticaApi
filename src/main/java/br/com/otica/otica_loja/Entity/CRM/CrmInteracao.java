package br.com.otica.otica_loja.Entity.CRM;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "crm_interacoes", schema = "loja")
public class CrmInteracao {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId; // FK para auth.users

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}