package br.com.otica.otica_loja.UseCases.newsletter;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.CRM.CampanhaEmail;
import br.com.otica.otica_loja.Entity.CRM.NewsletterAssinante;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.CRM.CampanhaEmailRepository;
import br.com.otica.otica_loja.Repository.CRM.NewsletterAssinanteRepository;

import br.com.otica.otica_loja.dto.EnviarCampanhaRequest;
import br.com.otica.otica_loja.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DispararCampanhaPersonalizadaUseCase {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NewsletterAssinanteRepository newsletterRepository;

    @Autowired
    private CampanhaEmailRepository campanhaRepository;

    public void executar(EnviarCampanhaRequest request) {
        // Usamos Set para evitar e-mails duplicados
        Set<String> destinatarios = new HashSet<>();

        switch (request.publicoAlvo().toUpperCase()) {
            case "TODOS_MEMBROS":
                // Puxa todos os usuários com conta criada na loja
                List<Usuario> usuarios = usuarioRepository.findAll();
                usuarios.forEach(u -> destinatarios.add(u.getEmail()));
                break;

            case "NEWSLETTER":
                // Puxa apenas as pessoas que deixaram o e-mail na Newsletter
                List<NewsletterAssinante> assinantes = newsletterRepository.findByAtivoTrue();
                assinantes.forEach(a -> destinatarios.add(a.getEmail()));
                break;

            case "AMBOS":
                // Cruza as duas bases
                usuarioRepository.findAll().forEach(u -> destinatarios.add(u.getEmail()));
                newsletterRepository.findByAtivoTrue().forEach(a -> destinatarios.add(a.getEmail()));
                break;

            case "ESPECIFICOS":
                // Utilizado caso o Admin queira disparar para uma lista manual do front
                if (request.emailsEspecificos() != null) {
                    destinatarios.addAll(request.emailsEspecificos());
                }
                break;

            default:
                throw new IllegalArgumentException("Público-alvo selecionado é inválido.");
        }

        if (destinatarios.isEmpty()) {
            throw new IllegalStateException("Nenhum destinatário encontrado para o público selecionado.");
        }

        // 1. Registra o histórico da campanha no CRM
        CampanhaEmail campanha = new CampanhaEmail();
        campanha.setTitulo(request.titulo());
        campanha.setAssunto(request.assunto());
        campanha.setHtmlContent(request.conteudoHtml());
        campanha.setEnviada(true);
        campanha.setAgendadaPara(OffsetDateTime.now());
        campanhaRepository.save(campanha);

        // 2. Dispara os e-mails chamando o método Assíncrono do EmailService
        for (String email : destinatarios) {
            if (email != null && !email.trim().isEmpty()) {
                // Certifique-se de que o método enviarEmailMarketing existe no seu EmailService
                emailService.enviarEmailMarketing(email, request.titulo(), request.assunto(), request.conteudoHtml());
            }
        }
    }
}