package br.com.otica.otica_loja.Entity.CRM;

import br.com.otica.otica_loja.enums.StatusAssinatura;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "assinaturas_clube", schema = "loja")
public class AssinaturaClube {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private UUID usuarioId; // FK para auth.users

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusAssinatura status = StatusAssinatura.PENDENTE;

    @Column(name = "data_inicio")
    private OffsetDateTime dataInicio;

    @Column(name = "data_renovacao")
    private OffsetDateTime dataRenovacao; // O dia que o admin vê para cobrar novamente

    @Column(name = "canal_pagamento", length = 50)
    private String canalPagamento; // Ex: "WhatsApp - PIX", "Boleto Externo"

    @Column(columnDefinition = "TEXT")
    private String anotacoesAdmin; // Notas internas (ex: Comprovante enviado via Telegram)

    @OneToMany(mappedBy = "assinatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoBeneficio> historicoBeneficios = new ArrayList<>();

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();
}