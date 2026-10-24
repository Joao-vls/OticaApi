package br.com.otica.otica_loja.UseCases.membros;

import br.com.otica.otica_loja.Entity.CRM.AssinaturaClube;
import br.com.otica.otica_loja.Repository.CRM.AssinaturaClubeRepository;
import br.com.otica.otica_loja.enums.StatusAssinatura;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j // Para logar no console do servidor
@Service
@RequiredArgsConstructor
public class AtualizarAssinaturasVencidasJob {

    private final AssinaturaClubeRepository assinaturaRepository;

    // Cron: Segundos Minutos Horas Dia-do-mes Mes Dia-da-semana
    // "0 0 2 * * *" = Roda todo dia exatamente às 02:00:00 da manhã
    @Scheduled(cron = "0 0 2 * * *", zone = "America/Sao_Paulo")
    @Transactional // Garante que todas as alterações sejam salvas juntas
    public void verificarEAtualizarVencidas() {
        log.info("Iniciando rotina automática: Verificação de assinaturas vencidas...");

        // Usamos o método do repository que criamos anteriormente
        List<AssinaturaClube> assinaturasVencidas = assinaturaRepository.findByStatusAndDataRenovacaoBefore(
                StatusAssinatura.ATIVA,
                OffsetDateTime.now()
        );

        if (!assinaturasVencidas.isEmpty()) {
            for (AssinaturaClube assinatura : assinaturasVencidas) {
                assinatura.setStatus(StatusAssinatura.VENCIDA);
                assinatura.setAtualizadoEm(OffsetDateTime.now());

                // Opcional: Adicionar um rastro de auditoria nas anotações
                String notaSistema = "\n[SISTEMA - " + OffsetDateTime.now() + "] Status alterado automaticamente para VENCIDA por falta de renovação.";
                String notaAnterior = assinatura.getAnotacoesAdmin() != null ? assinatura.getAnotacoesAdmin() : "";
                assinatura.setAnotacoesAdmin(notaAnterior + notaSistema);
            }

            // Salva todas as alterações em lote
            assinaturaRepository.saveAll(assinaturasVencidas);
            log.info("Sucesso: {} assinaturas foram marcadas como VENCIDAS.", assinaturasVencidas.size());
        } else {
            log.info("Nenhuma assinatura vencida foi encontrada hoje.");
        }
    }
}