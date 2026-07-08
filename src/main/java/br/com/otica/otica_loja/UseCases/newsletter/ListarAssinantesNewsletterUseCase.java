package br.com.otica.otica_loja.UseCases.newsletter;

import br.com.otica.otica_loja.Entity.CRM.NewsletterAssinante;
import br.com.otica.otica_loja.Repository.CRM.NewsletterAssinanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ListarAssinantesNewsletterUseCase {

    @Autowired
    private NewsletterAssinanteRepository assinanteRepository;

    /**
     * Lista todos os assinantes.
     */
    public List<NewsletterAssinante> listarTodos() {
        return assinanteRepository.findAll();
    }

    /**
     * Lista assinantes ativos.
     */
    public List<NewsletterAssinante> listarAtivos() {
        return assinanteRepository.findByAtivoTrue();
    }

    /**
     * Lista assinantes inativos.
     */
    public List<NewsletterAssinante> listarInativos() {
        return assinanteRepository.findByAtivoFalse();
    }

    /**
     * Lista assinantes por origem (ex.: campanha, site, indicação).
     */
    public List<NewsletterAssinante> listarPorOrigem(String origem) {
        return assinanteRepository.findByOrigem(origem);
    }

    /**
     * Lista assinantes criados após uma data.
     */
    public List<NewsletterAssinante> listarCriadosDepoisDe(OffsetDateTime data) {
        return assinanteRepository.findByCriadoEmAfter(data);
    }

    /**
     * Lista assinantes criados antes de uma data.
     */
    public List<NewsletterAssinante> listarCriadosAntesDe(OffsetDateTime data) {
        return assinanteRepository.findByCriadoEmBefore(data);
    }
}
