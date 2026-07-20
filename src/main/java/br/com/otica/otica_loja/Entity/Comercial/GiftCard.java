package br.com.otica.otica_loja.Entity.Comercial;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "gift_cards", schema = "loja")
public class GiftCard {

    // Getters e Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "valor_original", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorOriginal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "expira_em")
    private OffsetDateTime expiraEm;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

}
