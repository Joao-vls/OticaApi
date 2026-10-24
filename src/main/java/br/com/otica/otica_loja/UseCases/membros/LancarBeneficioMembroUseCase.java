package br.com.otica.otica_loja.UseCases.membros;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.CRM.AssinaturaClube;
import br.com.otica.otica_loja.Entity.CRM.HistoricoBeneficio;
import br.com.otica.otica_loja.Repository.CRM.AssinaturaClubeRepository;
import br.com.otica.otica_loja.Repository.CRM.HistoricoBeneficioRepository;
import br.com.otica.otica_loja.dto.LancarBeneficioRequestDTO;
import br.com.otica.otica_loja.enums.StatusAssinatura;
import br.com.otica.otica_loja.enums.TipoBeneficio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LancarBeneficioMembroUseCase {

    private final AssinaturaClubeRepository assinaturaRepository;
    private final HistoricoBeneficioRepository historicoRepository;

    @Transactional
    public HistoricoBeneficio executar(UUID usuarioId, LancarBeneficioRequestDTO dto) {
        AssinaturaClube assinatura = assinaturaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Este usuário não possui uma assinatura."));

        if (assinatura.getStatus() != StatusAssinatura.ATIVA) {
            throw new IllegalArgumentException("A assinatura não está ativa. Renove antes de lançar benefícios.");
        }

        // REGRA DE NEGÓCIO: Apenas 1 troca de óculos por ciclo vigente
        if (dto.tipo() == TipoBeneficio.TROCA_ANUAL_OCULOS) {
            List<HistoricoBeneficio> historicoTrocas = historicoRepository
                    .findByAssinaturaIdAndTipo(assinatura.getId(), TipoBeneficio.TROCA_ANUAL_OCULOS);

            // Verifica se alguma troca foi feita DEPOIS da data de início do ciclo atual
            boolean jaTrocouNoCicloAtual = historicoTrocas.stream()
                    .anyMatch(h -> h.getDataUtilizacao().isAfter(assinatura.getDataInicio()));

            if (jaTrocouNoCicloAtual) {
                throw new IllegalArgumentException("O cliente já utilizou a troca de óculos gratuita neste ciclo de assinatura.");
            }
        }

        // Recupera o ID do Admin que está logado fazendo a ação
        Usuario adminLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        HistoricoBeneficio beneficio = new HistoricoBeneficio();
        beneficio.setAssinatura(assinatura);
        beneficio.setTipo(dto.tipo());
        beneficio.setDescricao(dto.descricao());
        beneficio.setDataUtilizacao(OffsetDateTime.now());
        beneficio.setCriadoPorAdminId(adminLogado.getId());

        return historicoRepository.save(beneficio);
    }
}