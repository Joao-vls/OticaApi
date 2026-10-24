package br.com.otica.otica_loja.UseCases.membros;

import br.com.otica.otica_loja.Entity.CRM.AssinaturaClube;
import br.com.otica.otica_loja.Repository.CRM.AssinaturaClubeRepository;
import br.com.otica.otica_loja.enums.StatusAssinatura;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarRenovacoesPendentesUseCase {

    private final AssinaturaClubeRepository assinaturaRepository;

    public List<AssinaturaClube> listarVencendoNosProximosDias(int dias) {
        OffsetDateTime agora = OffsetDateTime.now();
        OffsetDateTime limite = agora.plusDays(dias);

        return assinaturaRepository.findByDataRenovacaoBetween(agora, limite);
    }

    public List<AssinaturaClube> listarVencidasPendentes() {
        return assinaturaRepository.findByStatusAndDataRenovacaoBefore(
                StatusAssinatura.ATIVA, // Ainda consta como ativa, mas a data já passou
                OffsetDateTime.now()
        );
    }
}