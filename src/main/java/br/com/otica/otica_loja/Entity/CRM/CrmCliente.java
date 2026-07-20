package br.com.otica.otica_loja.Entity.CRM;

import br.com.otica.otica_loja.enums.NivelCliente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "crm_clientes", schema = "loja")
public class CrmCliente {

    @Id
    @Column(name = "usuario_id")
    private UUID usuarioId; // FK para auth.users

    @Column(nullable = false)
    private Integer score = 0;

    @Column(name = "total_pedidos", nullable = false)
    private Integer totalPedidos = 0;

    @Column(name = "valor_gasto", precision = 14, scale = 2, nullable = false)
    private BigDecimal valorGasto = BigDecimal.ZERO;

    @Column(name = "ultimo_pedido_em")
    private OffsetDateTime ultimoPedidoEm;


    // Getters e Setters
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NivelCliente nivel = NivelCliente.BRONZE;


    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();


}