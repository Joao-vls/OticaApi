package br.com.otica.otica_loja.UseCases.newsletter;

import br.com.otica.otica_loja.Entity.CRM.CampanhaEmail;
import br.com.otica.otica_loja.Entity.CRM.NewsletterAssinante;
import br.com.otica.otica_loja.Repository.CRM.CampanhaEmailRepository;
import br.com.otica.otica_loja.Repository.CRM.NewsletterAssinanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EnviarCampanhaUseCase {

    @Autowired
    private CampanhaEmailRepository campanhaRepository;

    @Autowired
    private NewsletterAssinanteRepository assinanteRepository;

    /**
     * Envia uma campanha de e-mail para todos os assinantes ativos.
     */
    public void enviarCampanha(UUID campanhaId) {
        // 1. Buscar campanha
        CampanhaEmail campanha = campanhaRepository.findById(campanhaId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada."));

        if (Boolean.TRUE.equals(campanha.getEnviada())) {
            throw new IllegalStateException("A campanha já foi enviada.");
        }

        // 2. Buscar assinantes ativos
        List<NewsletterAssinante> assinantes = assinanteRepository.findByAtivoTrue();

        if (assinantes.isEmpty()) {
            throw new IllegalStateException("Nenhum assinante ativo para enviar a campanha.");
        }

        // 3. Simulação de envio (aqui você integraria com um serviço de e-mail real)
        for (NewsletterAssinante assinante : assinantes) {
            System.out.println("Enviando campanha '" + campanha.getTitulo() +
                    "' para: " + assinante.getEmail());
            // Aqui entraria a lógica de envio via SMTP ou API de e-mail (SendGrid, SES, etc.)
        }

        // 4. Atualizar campanha como enviada
        campanha.setEnviada(true);
        campanha.setAgendadaPara(OffsetDateTime.now());
        campanhaRepository.save(campanha);
    }
}
