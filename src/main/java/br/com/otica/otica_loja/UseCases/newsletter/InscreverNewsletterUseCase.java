package br.com.otica.otica_loja.UseCases.newsletter;

import br.com.otica.otica_loja.Entity.CRM.NewsletterAssinante;
import br.com.otica.otica_loja.Repository.CRM.NewsletterAssinanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class InscreverNewsletterUseCase {

    @Autowired
    private NewsletterAssinanteRepository assinanteRepository;

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
        return assinanteRepository.save(assinante);
    }
}
