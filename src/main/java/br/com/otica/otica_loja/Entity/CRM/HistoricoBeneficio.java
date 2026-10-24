package br.com.otica.otica_loja.Entity.CRM;

import br.com.otica.otica_loja.enums.TipoBeneficio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "historico_beneficios", schema = "loja")
public class HistoricoBeneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assinatura_id", nullable = false)
    private AssinaturaClube assinatura;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_beneficio", nullable = false, length = 50)
    private TipoBeneficio tipo;

    @Column(nullable = false)
    private String descricao; // Ex: "Resgatou a troca anual de óculos modelo XYZ"

    @Column(name = "data_utilizacao", nullable = false)
    private OffsetDateTime dataUtilizacao = OffsetDateTime.now();

    @Column(name = "criado_por_admin_id") // UUID do admin que lançou o benefício
    private UUID criadoPorAdminId;
}