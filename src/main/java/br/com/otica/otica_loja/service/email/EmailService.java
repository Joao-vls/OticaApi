package br.com.otica.otica_loja.service.email;

import br.com.otica.otica_loja.Entity.Admin.EmailTemplate;
import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Auth.Perfil;
import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Entity.Pedidos.PedidoItem;
import br.com.otica.otica_loja.Repository.Admin.EmailTemplateRepository;
import br.com.otica.otica_loja.Repository.Auth.EnderecoRepository;
import br.com.otica.otica_loja.Repository.Auth.PerfilRepository;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.enums.TipoEmail;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateRepository templateRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final EnderecoRepository enderecoRepository;
    private final PedidoRepository pedidoRepository; // Injetado para resolver o LazyInitializationException


    @Value("${app.mail.remetente}")
    private String remetente;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    public void enviarEmailVerificacao(String destinatario, String nome, String token) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setFrom(remetente, "Ótica App");
            helper.setTo(destinatario);
            helper.setSubject("Confirme seu e-mail - Ótica");

            String linkVerificacao = frontendUrl + "/usuario/verificar-email?token=" + token;

            String htmlMsg = "<div style='font-family: Arial, sans-serif; text-align: center; color: #333;'>"
                    + "<h2 style='color: #004d99;'>Olá, " + nome + "!</h2>"
                    + "<p>Obrigado por se cadastrar. Para ativar sua conta, clique no botão abaixo:</p>"
                    + "<a href='" + linkVerificacao + "' style='display: inline-block; padding: 12px 24px; color: white; background-color: #004d99; text-decoration: none; border-radius: 4px; font-weight: bold;'>Verificar E-mail</a>"
                    + "<p style='margin-top: 15px; font-size: 12px; color: #777;'>Este link expira em 24 horas.</p>"
                    + "</div>";

            helper.setText(htmlMsg, true);
            mailSender.send(mensagem);

            log.info("E-mail de verificação enviado com sucesso para {}", destinatario);

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de verificação para {}: {}", destinatario, e.getMessage());
        }
    }

    @Async
    public void enviarEmailDesbloqueio(String destinatario, String nome, String token) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setFrom(remetente, "Ótica App");
            helper.setTo(destinatario);
            helper.setSubject("Aviso de Segurança - Conta Bloqueada");

            String linkDesbloqueio = frontendUrl + "/usuario/desbloquear-conta?token=" + token;

            String htmlMsg = "<div style='font-family: Arial, sans-serif; text-align: center; color: #333;'>"
                    + "<h2 style='color: #dc3545;'>Olá, " + nome + ".</h2>"
                    + "<p>Identificamos múltiplas tentativas de login falhas na sua conta. Por segurança, o acesso foi temporariamente bloqueado.</p>"
                    + "<p>Para desbloquear sua conta, clique no botão abaixo:</p>"
                    + "<a href='" + linkDesbloqueio + "' style='display: inline-block; padding: 12px 24px; color: white; background-color: #dc3545; text-decoration: none; border-radius: 4px; font-weight: bold;'>Desbloquear Conta</a>"
                    + "</div>";

            helper.setText(htmlMsg, true);
            mailSender.send(mensagem);

            log.info("E-mail de desbloqueio enviado com sucesso para {}", destinatario);

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de desbloqueio para {}: {}", destinatario, e.getMessage());
        }
    }

    @Async
    public void enviarEmailRecuperacaoSenha(String destinatario, String nome, String token) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setFrom(remetente, "Ótica App");
            helper.setTo(destinatario);
            helper.setSubject("Redefinição de Senha - Ótica");

            String linkRecuperacao = frontendUrl + "/usuario/redefinir-senha?token=" + token;

            String htmlMsg = "<div style='font-family: Arial, sans-serif; text-align: center; color: #333;'>"
                    + "<h2 style='color: #28a745;'>Olá, " + nome + ".</h2>"
                    + "<p>Você solicitou a redefinição da sua senha. Clique no botão abaixo para criar uma nova senha:</p>"
                    + "<a href='" + linkRecuperacao + "' style='display: inline-block; padding: 12px 24px; color: #fff; background-color: #28a745; text-decoration: none; border-radius: 4px; font-weight: bold;'>Redefinir Senha</a>"
                    + "<p style='margin-top: 15px; font-size: 12px; color: #777;'>Este link expira em 2 horas.</p>"
                    + "</div>";

            helper.setText(htmlMsg, true);
            mailSender.send(mensagem);

            log.info("E-mail de recuperação de senha enviado com sucesso para {}", destinatario);

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de recuperação para {}: {}", destinatario, e.getMessage());
        }
    }

    @Async
    @Transactional(readOnly = true) // Abre uma sessão Hibernate na thread do @Async evitando o LazyInitializationException
    public void enviarEmailPedido(Pedido pedidoParam, TipoEmail tipo, String extraInfo) {
        try {
            // Re-anexa o pedido na sessão atual do banco para poder carregar a lista de Itens
            Pedido pedido = pedidoRepository.findById(pedidoParam.getId()).orElse(pedidoParam);

            String emailDestino = pedido.getClienteEmail();
            String nomeCliente = pedido.getClienteNome();

            if (pedido.getUsuarioId() != null) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(pedido.getUsuarioId());
                if (usuarioOpt.isPresent()) {
                    emailDestino = usuarioOpt.get().getEmail();
                    Optional<Perfil> perfilOpt = perfilRepository.findById(pedido.getUsuarioId());
                    nomeCliente = perfilOpt.map(Perfil::getNome).orElse(usuarioOpt.get().getNome());
                }
            }

            if (emailDestino == null || emailDestino.isBlank()) {
                log.warn("E-mail de pedido #{} não enviado: Destinatário sem e-mail cadastrado.", pedido.getId());
                return;
            }

            String numPedidoExibicao = pedido.getNumero() != null ? String.valueOf(pedido.getNumero()) : pedido.getId().toString().substring(0, 8).toUpperCase();

            Optional<EmailTemplate> optTemplate = templateRepository.findByTipo(tipo);
            String assunto = (optTemplate.isPresent() && Boolean.TRUE.equals(optTemplate.get().getAtivo()))
                    ? optTemplate.get().getAssunto().replace("{{NUMERO_PEDIDO}}", numPedidoExibicao)
                    : getAssuntoPadrao(tipo, numPedidoExibicao);

            String htmlBase = (optTemplate.isPresent() && Boolean.TRUE.equals(optTemplate.get().getAtivo()))
                    ? optTemplate.get().getHtmlBase()
                    : getHtmlPadrao(tipo);

            Map<String, String> variaveis = montarVariaveisPedido(pedido, nomeCliente, numPedidoExibicao, tipo, extraInfo);

            for (Map.Entry<String, String> entry : variaveis.entrySet()) {
                htmlBase = htmlBase.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setFrom(remetente, "Ótica App");
            helper.setTo(emailDestino);
            helper.setSubject(assunto);
            helper.setText(htmlBase, true);

            mailSender.send(mensagem);
            log.info("E-mail do tipo {} enviado com sucesso para o pedido #{}", tipo, numPedidoExibicao);

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de pedido #{}: {}", pedidoParam.getId(), e.getMessage(), e);
        }
    }

    private Map<String, String> montarVariaveisPedido(Pedido pedido, String nomeCliente, String numPedido, TipoEmail tipo, String extraInfo) {
        Map<String, String> vars = new HashMap<>();

        vars.put("NOME_CLIENTE", nomeCliente != null ? nomeCliente : "Cliente");
        vars.put("NUMERO_PEDIDO", numPedido);
        vars.put("VALOR_SUBTOTAL", String.format("R$ %.2f", pedido.getSubtotal()));
        vars.put("VALOR_DESCONTO", String.format("R$ %.2f", pedido.getDesconto()));
        vars.put("VALOR_FRETE", String.format("R$ %.2f", pedido.getFrete()));
        vars.put("VALOR_TOTAL", String.format("R$ %.2f", pedido.getTotal()));
        vars.put("DATA_PEDIDO", pedido.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")));
        vars.put("URL_LOJA", frontendUrl);

        vars.put("STEPPER_STATUS", gerarStepperHTML(tipo));

        StringBuilder tabelaProdutos = new StringBuilder();
        tabelaProdutos.append("<table width='100%' style='border-collapse: collapse; margin-top: 15px; font-family: Arial, sans-serif;'>");
        tabelaProdutos.append("<tr style='background-color: #f1f3f5; border-bottom: 2px solid #dee2e6; color: #495057; font-size: 13px;'>")
                .append("<th style='padding: 10px; text-align: left;'>Produto</th>")
                .append("<th style='padding: 10px; text-align: center;'>Qtd.</th>")
                .append("<th style='padding: 10px; text-align: right;'>Preço Unit.</th>")
                .append("<th style='padding: 10px; text-align: right;'>Subtotal</th>")
                .append("</tr>");

        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            for (PedidoItem item : pedido.getItens()) {
                String imgHtml = (item.getImagemUrl() != null && !item.getImagemUrl().isBlank())
                        ? "<img src='" + item.getImagemUrl() + "' width='45' height='45' style='object-fit: contain; vertical-align: middle; margin-right: 10px; border-radius: 4px;'>"
                        : "";

                tabelaProdutos.append("<tr style='border-bottom: 1px solid #e9ecef; font-size: 14px;'>")
                        .append("<td style='padding: 10px; color: #212529;'>").append(imgHtml).append("<b>").append(item.getNomeProduto()).append("</b></td>")
                        .append("<td style='padding: 10px; text-align: center; color: #495057;'>").append(item.getQuantidade()).append("</td>")
                        .append("<td style='padding: 10px; text-align: right; color: #495057;'>R$ ").append(String.format("%.2f", item.getPrecoUnitario())).append("</td>")
                        .append("<td style='padding: 10px; text-align: right; color: #212529; font-weight: bold;'>R$ ").append(String.format("%.2f", item.getSubtotal())).append("</td>")
                        .append("</tr>");
            }
        }
        tabelaProdutos.append("</table>");
        vars.put("TABELA_PRODUTOS", tabelaProdutos.toString());

        if (pedido.getEnderecoId() != null) {
            Optional<Endereco> endOpt = enderecoRepository.findById(pedido.getEnderecoId());
            if (endOpt.isPresent()) {
                Endereco end = endOpt.get();
                String endHtml = "<b>" + end.getNomeRecebedor() + "</b><br>" +
                        end.getLogradouro() + ", nº " + end.getNumero() + (end.getComplemento() != null ? " (" + end.getComplemento() + ")" : "") + "<br>" +
                        "Bairro: " + end.getBairro() + "<br>" +
                        "Cidade/UF: " + end.getCidade() + " / " + end.getEstado() + "<br>" +
                        "CEP: " + end.getCep();
                vars.put("ENDERECO_ENTREGA", endHtml);
            } else {
                vars.put("ENDERECO_ENTREGA", "Endereço cadastrado no pedido.");
            }
        } else {
            vars.put("ENDERECO_ENTREGA", "<b>Venda Presencial / Retirada em Balcão</b>");
        }

        vars.put("INFORMACOES_EXTRAS", extraInfo != null ? extraInfo : "");

        return vars;
    }

    private String gerarStepperHTML(TipoEmail tipo) {
        boolean pRec = true;
        boolean pAprov = tipo == TipoEmail.PAGAMENTO_APROVADO || tipo == TipoEmail.PEDIDO_ENTREGUE;
        boolean pEntregue = tipo == TipoEmail.PEDIDO_ENTREGUE;
        boolean pCancelado = tipo == TipoEmail.PEDIDO_CANCELADO;

        if (pCancelado) {
            return "<div style='background: #fff5f5; border: 1px solid #feb2b2; color: #c53030; padding: 12px; border-radius: 6px; text-align: center; font-weight: bold; margin: 20px 0;'>" +
                    "❌ Este pedido foi cancelado." +
                    "</div>";
        }

        return "<table width='100%' style='margin: 20px 0; text-align: center; font-family: Arial, sans-serif; font-size: 12px; border-collapse: collapse;'>" +
                "<tr>" +
                "<td style='width: 33%; vertical-align: top; color: " + (pRec ? "#28a745" : "#6c757d") + ";'>" +
                "<div style='font-size: 18px; margin-bottom: 4px;'>" + (pRec ? "✔" : "○") + "</div><b>Pedido Recebido</b>" +
                "</td>" +
                "<td style='width: 33%; vertical-align: top; color: " + (pAprov ? "#28a745" : "#6c757d") + ";'>" +
                "<div style='font-size: 18px; margin-bottom: 4px;'>" + (pAprov ? "✔" : "○") + "</div><b>Pagamento Aprovado</b>" +
                "</td>" +
                "<td style='width: 33%; vertical-align: top; color: " + (pEntregue ? "#28a745" : "#6c757d") + ";'>" +
                "<div style='font-size: 18px; margin-bottom: 4px;'>" + (pEntregue ? "✔" : "○") + "</div><b>Pedido Concluído</b>" +
                "</td>" +
                "</tr>" +
                "</table>";
    }

    private String getAssuntoPadrao(TipoEmail tipo, String numPedido) {
        return switch (tipo) {
            case PEDIDO_REALIZADO -> "Pedido #" + numPedido + " realizado com sucesso!";
            case PAGAMENTO_APROVADO -> "Pagamento aprovado - Pedido #" + numPedido;
            case PEDIDO_ENTREGUE -> "Pedido #" + numPedido + ": Pedido concluído.";
            case PEDIDO_CANCELADO -> "Pedido #" + numPedido + " foi cancelado.";
        };
    }

    private String getHtmlPadrao(TipoEmail tipo) {
        String header = "<div style='background-color: #004d99; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;'>" +
                "<h1 style='color: #ffffff; margin: 0; font-family: Arial, sans-serif; font-size: 24px; letter-spacing: 1px;'>ÓTICA</h1>" +
                "</div>" +
                "<div style='padding: 24px; background-color: #ffffff; font-family: Arial, sans-serif; border: 1px solid #e9ecef; border-top: none; border-radius: 0 0 8px 8px; color: #333333;'>" +
                "<h2 style='text-align: center; color: #ff5722; margin-top: 0;'>Informações sobre o pedido: {{NUMERO_PEDIDO}}</h2>" +
                "<p style='font-size: 16px;'>Olá, <b>{{NOME_CLIENTE}}</b>!</p>";

        String statusMensagem = switch (tipo) {
            case PEDIDO_REALIZADO -> "<p>O seu pedido <b>#{{NUMERO_PEDIDO}}</b> foi realizado com sucesso em {{DATA_PEDIDO}}!</p>{{INFORMACOES_EXTRAS}}";
            case PAGAMENTO_APROVADO -> "<p>Identificamos a confirmação do seu pagamento. O seu pedido <b>#{{NUMERO_PEDIDO}}</b> agora está em preparação!</p>";
            case PEDIDO_ENTREGUE -> "<p>O status atual do seu pedido é: <b style='color: #28a745;'>Pedido concluído.</b></p>";
            case PEDIDO_CANCELADO -> "<p>Informamos que o seu pedido <b>#{{NUMERO_PEDIDO}}</b> foi cancelado.</p>";
        };

        String resumoValores = "<table width='100%' style='margin-top: 15px; font-family: Arial, sans-serif; font-size: 14px; text-align: right;'>" +
                "<tr><td style='padding: 4px; color: #6c757d;'>Subtotal:</td><td style='padding: 4px; width: 120px;'>{{VALOR_SUBTOTAL}}</td></tr>" +
                "<tr><td style='padding: 4px; color: #6c757d;'>Desconto:</td><td style='padding: 4px; color: #28a745;'>- {{VALOR_DESCONTO}}</td></tr>" +
                "<tr><td style='padding: 4px; color: #6c757d;'>Frete:</td><td style='padding: 4px;'>{{VALOR_FRETE}}</td></tr>" +
                "<tr style='font-size: 16px; font-weight: bold;'><td style='padding: 8px 4px 0 4px; color: #004d99;'>Total:</td><td style='padding: 8px 4px 0 4px; color: #004d99;'>{{VALOR_TOTAL}}</td></tr>" +
                "</table>";

        String detalhesPedido = "{{STEPPER_STATUS}}" +
                "<h3 style='color: #004d99; margin-top: 25px; border-bottom: 2px solid #004d99; padding-bottom: 5px;'>Itens do Pedido</h3>" +
                "{{TABELA_PRODUTOS}}" +
                resumoValores +
                "<h3 style='color: #004d99; margin-top: 25px; border-bottom: 2px solid #004d99; padding-bottom: 5px;'>Endereço de Entrega</h3>" +
                "<p style='color: #495057; line-height: 1.5;'>{{ENDERECO_ENTREGA}}</p>";

        String footer = "<br><hr style='border: none; border-top: 1px solid #e9ecef;'><p style='text-align: center; color: #868e96; font-size: 13px; margin-bottom: 0;'>Obrigado por comprar conosco!<br><b>Equipe Ótica</b></p></div>";

        return header + statusMensagem + detalhesPedido + footer;
    }
}