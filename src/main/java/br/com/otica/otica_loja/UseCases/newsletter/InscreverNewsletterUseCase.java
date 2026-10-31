package br.com.otica.otica_loja.UseCases.newsletter;

import br.com.otica.otica_loja.Entity.CRM.NewsletterAssinante;
import br.com.otica.otica_loja.Repository.CRM.NewsletterAssinanteRepository;
import br.com.otica.otica_loja.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class InscreverNewsletterUseCase {

    @Autowired
    private NewsletterAssinanteRepository assinanteRepository;

    @Autowired
    private EmailService emailService; // 🔥 Injeção do serviço de e-mail adicionada

    /**
     * Inscreve um novo assinante na newsletter.
     */
    public NewsletterAssinante inscrever(String email, String origem) {
        // 1. Validar duplicidade
        if (assinanteRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe um assinante com este e-mail.");
        }

        // 2. Criar assinante
        NewsletterAssinante assinante = new NewsletterAssinante();
        assinante.setEmail(email);
        assinante.setOrigem(origem);
        assinante.setAtivo(true);
        assinante.setCriadoEm(OffsetDateTime.now());

        // 3. Persistir
        NewsletterAssinante salvo = assinanteRepository.save(assinante);

        // 4. Disparar E-mail de Boas Vindas em background (Async) 🔥
        emailService.enviarEmailBoasVindasNewsletter(email);

        return salvo;
    }
}