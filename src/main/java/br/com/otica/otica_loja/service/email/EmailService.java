package br.com.otica.otica_loja.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async // Executa em background para não travar a requisição do usuário
    public void enviarEmailVerificacao(String destinatario, String nome, String token) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            //helper.setFrom(remetente);
            helper.setFrom("joao.leite.lvj@gmail.com", "Ótica App");
            helper.setSubject("Confirme seu e-mail - Ótica");

            // Rota atualizada para bater no frontend do Angular
            String linkVerificacao = frontendUrl + "/usuario/verificar-email?token=" + token;

            String htmlMsg = "<div style='font-family: Arial, sans-serif; text-align: center;'>"
                    + "<h2>Olá, " + nome + "!</h2>"
                    + "<p>Obrigado por se cadastrar. Para ativar sua conta, clique no botão abaixo:</p>"
                    + "<a href='" + linkVerificacao + "' style='display: inline-block; padding: 10px 20px; color: white; background-color: #007bff; text-decoration: none; border-radius: 5px;'>Verificar E-mail</a>"
                    + "<p>Se o botão não funcionar, copie e cole o link no seu navegador:</p>"
                    + "<p><a href='" + linkVerificacao + "'>" + linkVerificacao + "</a></p>"
                    + "<p>Este link expira em 24 horas.</p>"
                    + "</div>";

            helper.setText(htmlMsg, true);
            mailSender.send(mensagem);

            log.info("E-mail de verificação enviado para {}", destinatario);

        }catch (Exception e) { // <-- MUDE AQUI DE MessagingException PARA Exception
            log.error("Erro ao enviar e-mail para {}: {}", destinatario, e.getMessage());
        }
    }

    @Async
    public void enviarEmailDesbloqueio(String destinatario, String nome, String token) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            //helper.setFrom(remetente);
            helper.setFrom("joao.leite.lvj@gmail.com", "Ótica App");
            helper.setTo(destinatario);
            helper.setSubject("Aviso de Segurança - Conta Bloqueada");

            // Rota atualizada para bater no frontend do Angular
            String linkDesbloqueio = frontendUrl + "/usuario/desbloquear-conta?token=" + token;

            String htmlMsg = "<div style='font-family: Arial, sans-serif; text-align: center;'>"
                    + "<h2>Olá, " + nome + ".</h2>"
                    + "<p>Identificamos múltiplas tentativas de login falhas na sua conta. Por segurança, o acesso foi temporariamente bloqueado.</p>"
                    + "<p>Para desbloquear sua conta, clique no botão abaixo:</p>"
                    + "<a href='" + linkDesbloqueio + "' style='display: inline-block; padding: 10px 20px; color: white; background-color: #dc3545; text-decoration: none; border-radius: 5px;'>Desbloquear Conta</a>"
                    + "<p>Se não foi você que tentou acessar, recomendamos trocar sua senha imediatamente após o desbloqueio.</p>"
                    + "</div>";

            helper.setText(htmlMsg, true);
            mailSender.send(mensagem);

            log.info("E-mail de desbloqueio enviado para {}", destinatario);

        } catch (Exception e) { // <-- MUDE AQUI DE MessagingException PARA Exception
            log.error("Erro ao enviar e-mail para {}: {}", destinatario, e.getMessage());
        }
    }

    @Async
    public void enviarEmailRecuperacaoSenha(String destinatario, String nome, String token) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            //helper.setFrom(remetente);
            helper.setFrom("joao.leite.lvj@gmail.com", "Ótica App");
            helper.setTo(destinatario);
            helper.setSubject("Redefinição de Senha - Ótica");

            // Rota do Angular onde o usuário digitará a nova senha
            String linkRecuperacao = frontendUrl + "/usuario/redefinir-senha?token=" + token;

            String htmlMsg = "<div style='font-family: Arial, sans-serif; text-align: center;'>"
                    + "<h2>Olá, " + nome + ".</h2>"
                    + "<p>Você solicitou a redefinição da sua senha. Clique no botão abaixo para criar uma nova senha:</p>"
                    + "<a href='" + linkRecuperacao + "' style='display: inline-block; padding: 10px 20px; color: #fff; background-color: #28a745; text-decoration: none; border-radius: 5px;'>Redefinir Senha</a>"
                    + "<p>Se você não solicitou essa alteração, pode ignorar este e-mail com segurança.</p>"
                    + "<p>Este link expira em 2 horas.</p>"
                    + "</div>";

            helper.setText(htmlMsg, true);
            mailSender.send(mensagem);

            log.info("E-mail de recuperação de senha enviado para {}", destinatario);

        } catch (Exception e) { // <-- MUDE AQUI DE MessagingException PARA Exception
            log.error("Erro ao enviar e-mail para {}: {}", destinatario, e.getMessage());
        }
    }
}