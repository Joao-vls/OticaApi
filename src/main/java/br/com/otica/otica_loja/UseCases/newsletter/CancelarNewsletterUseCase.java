package br.com.otica.otica_loja.UseCases.newsletter;

import br.com.otica.otica_loja.Entity.CRM.NewsletterAssinante;
import br.com.otica.otica_loja.Repository.CRM.NewsletterAssinanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CancelarNewsletterUseCase {

    @Autowired
    private NewsletterAssinanteRepository assinanteRepository;

    /**
     * Cancela a assinatura da newsletter pelo e-mail.
     */
    public NewsletterAssinante cancelar(String email) {
        // 1. Buscar assinante
        NewsletterAssinante assinante = assinanteRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Assinante não encontrado."));

        if (!assinante.getAtivo()) {
            throw new IllegalStateException("Este assinante já está inativo.");
        }

        // 2. Atualizar status para inativo
        assinante.setAtivo(false);

        // 3. Persistir
        return assinanteRepository.save(assinante);
    }
}
