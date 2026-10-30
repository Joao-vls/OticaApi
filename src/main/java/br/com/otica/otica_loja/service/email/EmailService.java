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
import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
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
import java.util.List;
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
    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository; // 👈 Adicionado para resolver os itens em branco

    @Value("${app.mail.remetente}")
    private String remetente;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    // Cores padrão do sistema mapeadas para variáveis Java
    private final String MAIN_TEXT = "#001325";
    private final String SECONDARY_TEXT = "#ff0000";
    private final String MAIN_BG = "#f0f0f0";
    private final String SECONDARY_BG = "#e6e5e4";
    private final String CARD_BG = "#ffffff";

    @Async
    public void enviarEmailVerificacao(String destinatario, String nome, String token) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setFrom(remetente, "Ótica");
            helper.setTo(destinatario);
            helper.setSubject("Confirme seu e-mail");

            String link = frontendUrl + "/usuario/verificar-email?token=" + token;

            String htmlMsg = gerarTemplateBase(
                    "BEM-VINDO À ÓTICA",
                    "<h2 style='color: " + MAIN_TEXT + "; margin-bottom: 20px;'>Olá, " + nome + "!</h2>" +
                            "<p style='color: #555; line-height: 1.6; margin-bottom: 30px;'>Ficamos muito felizes com o seu cadastro. Para ativar sua conta e aproveitar todas as nossas novidades, confirme seu e-mail no botão abaixo:</p>" +
                            "<a href='" + link + "' style='display: inline-block; padding: 14px 30px; color: #ffffff; background-color: " + MAIN_TEXT + "; text-decoration: none; border-radius: 4px; font-weight: bold; text-transform: uppercase; letter-spacing: 1px;'>Verificar E-mail</a>" +
                            "<p style='margin-top: 30px; font-size: 12px; color: #999;'>Este link é válido por 24 horas.</p>"
            );

            helper.setText(htmlMsg, true);
            mailSender.send(mensagem);
            log.info("E-mail de verificação enviado para {}", destinatario);
        } catch (Exception e) {
            log.error("Erro ao enviar verificação para {}: {}", destinatario, e.getMessage());
        }
    }

    @Async
    public void enviarEmailDesbloqueio(String destinatario, String nome, String token) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setFrom(remetente, "Ótica");
            helper.setTo(destinatario);
            helper.setSubject("Aviso de Segurança - Conta Bloqueada");

            String link = frontendUrl + "/usuario/desbloquear-conta?token=" + token;

            String htmlMsg = gerarTemplateBase(
                    "ALERTA DE SEGURANÇA",
                    "<h2 style='color: " + SECONDARY_TEXT + "; margin-bottom: 20px;'>Olá, " + nome + ".</h2>" +
                            "<p style='color: #555; line-height: 1.6; margin-bottom: 30px;'>Identificamos múltiplas tentativas falhas de acesso à sua conta. Por medida de segurança, seu login foi temporariamente bloqueado.</p>" +
                            "<a href='" + link + "' style='display: inline-block; padding: 14px 30px; color: #ffffff; background-color: " + SECONDARY_TEXT + "; text-decoration: none; border-radius: 4px; font-weight: bold; text-transform: uppercase; letter-spacing: 1px;'>Desbloquear Minha Conta</a>"
            );

            helper.setText(htmlMsg, true);
            mailSender.send(mensagem);
        } catch (Exception e) {
            log.error("Erro ao enviar desbloqueio: {}", e.getMessage());
        }
    }

    @Async
    public void enviarEmailRecuperacaoSenha(String destinatario, String nome, String token) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setFrom(remetente, "Ótica");
            helper.setTo(destinatario);
            helper.setSubject("Redefinição de Senha");

            String link = frontendUrl + "/usuario/redefinir-senha?token=" + token;

            String htmlMsg = gerarTemplateBase(
                    "REDEFINIR SENHA",
                    "<h2 style='color: " + MAIN_TEXT + "; margin-bottom: 20px;'>Olá, " + nome + ".</h2>" +
                            "<p style='color: #555; line-height: 1.6; margin-bottom: 30px;'>Recebemos uma solicitação para alterar a senha da sua conta. Caso tenha sido você, clique no botão abaixo para cadastrar uma nova senha segura:</p>" +
                            "<a href='" + link + "' style='display: inline-block; padding: 14px 30px; color: #ffffff; background-color: " + MAIN_TEXT + "; text-decoration: none; border-radius: 4px; font-weight: bold; text-transform: uppercase; letter-spacing: 1px;'>Criar Nova Senha</a>" +
                            "<p style='margin-top: 30px; font-size: 12px; color: #999;'>Se não foi você, apenas ignore este e-mail. O link expira em 2 horas.</p>"
            );

            helper.setText(htmlMsg, true);
            mailSender.send(mensagem);
        } catch (Exception e) {
            log.error("Erro ao enviar recuperação de senha: {}", e.getMessage());
        }
    }

    @Async
    @Transactional(readOnly = true)
    public void enviarEmailPedido(Pedido pedidoParam, TipoEmail tipo, String extraInfo) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoParam.getId()).orElse(pedidoParam);
            // 🔥 BUSCA FORÇADA DOS ITENS NO BANCO PARA EVITAR A LISTA VAZIA
            List<PedidoItem> itensDoPedido = pedidoItemRepository.findByPedido(pedido);

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

            if (emailDestino == null || emailDestino.isBlank()) return;

            String numPedidoExibicao = pedido.getNumero() != null ? String.valueOf(pedido.getNumero()) : pedido.getId().toString().substring(0, 8).toUpperCase();

            Optional<EmailTemplate> optTemplate = templateRepository.findByTipo(tipo);
            String assunto = (optTemplate.isPresent() && Boolean.TRUE.equals(optTemplate.get().getAtivo()))
                    ? optTemplate.get().getAssunto().replace("{{NUMERO_PEDIDO}}", numPedidoExibicao)
                    : getAssuntoPadrao(tipo, numPedidoExibicao);

            String htmlBase = (optTemplate.isPresent() && Boolean.TRUE.equals(optTemplate.get().getAtivo()))
                    ? optTemplate.get().getHtmlBase()
                    : getHtmlPadraoPedido(tipo);

            Map<String, String> variaveis = montarVariaveisPedido(pedido, itensDoPedido, nomeCliente, numPedidoExibicao, tipo, extraInfo);

            for (Map.Entry<String, String> entry : variaveis.entrySet()) {
                htmlBase = htmlBase.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            // Envolve as variáveis do pedido no nosso Layout Elegante Base
            String finalHtml = gerarTemplateBase("ATUALIZAÇÃO DO PEDIDO", htmlBase);

            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setFrom(remetente, "Ótica");
            helper.setTo(emailDestino);
            helper.setSubject(assunto);
            helper.setText(finalHtml, true);

            mailSender.send(mensagem);
            log.info("E-mail de pedido {} enviado para {}", tipo, emailDestino);

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de pedido: {}", e.getMessage(), e);
        }
    }

    private Map<String, String> montarVariaveisPedido(Pedido pedido, List<PedidoItem> itens, String nomeCliente, String numPedido, TipoEmail tipo, String extraInfo) {
        Map<String, String> vars = new HashMap<>();

        vars.put("NOME_CLIENTE", nomeCliente != null ? nomeCliente : "Cliente");
        vars.put("NUMERO_PEDIDO", numPedido);
        vars.put("VALOR_SUBTOTAL", String.format("R$ %.2f", pedido.getSubtotal()));
        vars.put("VALOR_DESCONTO", String.format("R$ %.2f", pedido.getDesconto()));
        vars.put("VALOR_FRETE", String.format("R$ %.2f", pedido.getFrete()));
        vars.put("VALOR_TOTAL", String.format("R$ %.2f", pedido.getTotal()));
        vars.put("DATA_PEDIDO", pedido.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")));
        vars.put("STEPPER_STATUS", gerarStepperModernoHTML(tipo));

        // 🔥 MONTAGEM ELEGANTE DA TABELA DE PRODUTOS
        StringBuilder tb = new StringBuilder();
        tb.append("<table width='100%' style='border-collapse: collapse; margin-top: 20px; font-family: Arial, Helvetica, sans-serif;'>");
        tb.append("<thead style='background-color: ").append(SECONDARY_BG).append("; text-transform: uppercase; font-size: 11px; letter-spacing: 1px;'>")
                .append("<tr>")
                .append("<th style='padding: 12px; text-align: left; color: ").append(MAIN_TEXT).append(";'>Produto</th>")
                .append("<th style='padding: 12px; text-align: center; color: ").append(MAIN_TEXT).append(";'>Qtd</th>")
                .append("<th style='padding: 12px; text-align: right; color: ").append(MAIN_TEXT).append(";'>Subtotal</th>")
                .append("</tr></thead><tbody>");

        if (itens != null && !itens.isEmpty()) {
            for (PedidoItem item : itens) {
                // Tratamento de URL da imagem para garantir que apareça no e-mail
                String urlImg = item.getImagemUrl();
                if (urlImg != null && !urlImg.startsWith("http")) {
                    urlImg = frontendUrl + (urlImg.startsWith("/") ? "" : "/") + urlImg;
                }

                String imgTag = (urlImg != null && !urlImg.isBlank())
                        ? "<img src='" + urlImg + "' width='50' style='max-width: 50px; border-radius: 4px; vertical-align: middle; margin-right: 15px; border: 1px solid " + SECONDARY_BG + ";'>"
                        : "";

                tb.append("<tr style='border-bottom: 1px solid ").append(SECONDARY_BG).append(";'>")
                        .append("<td style='padding: 15px 12px; color: ").append(MAIN_TEXT).append("; font-size: 14px;'>").append(imgTag).append("<span style='vertical-align: middle;'>").append(item.getNomeProduto()).append("</span></td>")
                        .append("<td style='padding: 15px 12px; text-align: center; color: #555; font-size: 14px;'>").append(item.getQuantidade()).append("</td>")
                        .append("<td style='padding: 15px 12px; text-align: right; color: ").append(MAIN_TEXT).append("; font-weight: bold; font-size: 14px;'>R$ ").append(String.format("%.2f", item.getSubtotal())).append("</td>")
                        .append("</tr>");
            }
        } else {
            tb.append("<tr><td colspan='3' style='padding: 15px; text-align: center; color: #999;'>Detalhes dos itens sendo processados...</td></tr>");
        }
        tb.append("</tbody></table>");
        vars.put("TABELA_PRODUTOS", tb.toString());

        // MONTAGEM DO ENDEREÇO
        if (pedido.getEnderecoId() != null) {
            Optional<Endereco> endOpt = enderecoRepository.findById(pedido.getEnderecoId());
            if (endOpt.isPresent()) {
                Endereco end = endOpt.get();
                vars.put("ENDERECO_ENTREGA", "<b>" + end.getNomeRecebedor() + "</b><br>" + end.getLogradouro() + ", nº " + end.getNumero() + (end.getComplemento() != null ? " (" + end.getComplemento() + ")" : "") + "<br>" + end.getBairro() + " - " + end.getCidade() + "/" + end.getEstado() + "<br>CEP: " + end.getCep());
            } else {
                vars.put("ENDERECO_ENTREGA", "Endereço cadastrado.");
            }
        } else {
            vars.put("ENDERECO_ENTREGA", "<b>Retirada em Loja (Balcão)</b>");
        }

        vars.put("INFORMACOES_EXTRAS", extraInfo != null ? extraInfo : "");
        return vars;
    }

    private String gerarStepperModernoHTML(TipoEmail tipo) {
        if (tipo == TipoEmail.PEDIDO_CANCELADO) {
            return "<div style='background-color: #fff0f0; border-left: 4px solid " + SECONDARY_TEXT + "; padding: 15px; margin: 25px 0; border-radius: 4px;'>" +
                    "<span style='color: " + SECONDARY_TEXT + "; font-weight: bold; font-size: 14px; text-transform: uppercase;'>✗ Pedido Cancelado</span>" +
                    "</div>";
        }

        boolean s2 = tipo == TipoEmail.PAGAMENTO_APROVADO || tipo == TipoEmail.PEDIDO_ENTREGUE;
        boolean s3 = tipo == TipoEmail.PEDIDO_ENTREGUE;

        String color1 = MAIN_TEXT;
        String color2 = s2 ? MAIN_TEXT : "#cccccc";
        String color3 = s3 ? MAIN_TEXT : "#cccccc";

        return "<table width='100%' style='margin: 30px 0; text-align: center; font-family: Arial, sans-serif; font-size: 11px; text-transform: uppercase; letter-spacing: 1px; border-collapse: collapse;'>" +
                "<tr>" +
                "<td style='width: 33%; color: " + color1 + "; font-weight: bold;'><div style='font-size: 20px; margin-bottom: 8px;'>●</div>PEDIDO REALIZADO</td>" +
                "<td style='width: 33%; color: " + color2 + "; font-weight: bold;'><div style='font-size: 20px; margin-bottom: 8px;'>" + (s2 ? "●" : "○") + "</div>PAGAMENTO APROVADO</td>" +
                "<td style='width: 33%; color: " + color3 + "; font-weight: bold;'><div style='font-size: 20px; margin-bottom: 8px;'>" + (s3 ? "●" : "○") + "</div>PEDIDO ENTREGUE</td>" +
                "</tr></table>";
    }

    private String getAssuntoPadrao(TipoEmail tipo, String numPedido) {
        return switch (tipo) {
            case PEDIDO_REALIZADO -> "Pedido #" + numPedido + " recebido com sucesso.";
            case PAGAMENTO_APROVADO -> "Pagamento aprovado! Pedido #" + numPedido;
            case PEDIDO_ENTREGUE -> "Oba! Seu pedido #" + numPedido + " foi entregue.";
            case PEDIDO_CANCELADO -> "Atualização: O pedido #" + numPedido + " foi cancelado.";
        };
    }

    private String getHtmlPadraoPedido(TipoEmail tipo) {
        String titulo = "<h2 style='text-align: center; color: " + MAIN_TEXT + "; margin-top: 0; font-size: 24px; font-weight: bold;'>PEDIDO <span style='color: " + SECONDARY_TEXT + ";'>#{{NUMERO_PEDIDO}}</span></h2>" +
                "<p style='text-align: center; font-size: 16px; color: #555;'>Olá, <b>{{NOME_CLIENTE}}</b>.</p>";

        String statusMensagem = switch (tipo) {
            case PEDIDO_REALIZADO -> "<p style='text-align: center; line-height: 1.6; color: #555;'>Seu pedido foi realizado em {{DATA_PEDIDO}} e estamos aguardando a confirmação do pagamento para iniciar a separação.</p>{{INFORMACOES_EXTRAS}}";
            case PAGAMENTO_APROVADO -> "<p style='text-align: center; line-height: 1.6; color: #555;'>Tudo certo com o seu pagamento! O seu pedido já está na fila de preparação e envio.</p>";
            case PEDIDO_ENTREGUE -> "<p style='text-align: center; line-height: 1.6; color: #555;'>Sua entrega foi concluída. Esperamos que você goste dos produtos!</p>";
            case PEDIDO_CANCELADO -> "<p style='text-align: center; line-height: 1.6; color: #555;'>Informamos que o seu pedido precisou ser cancelado.</p>";
        };

        String resumoValores = "<table width='100%' style='margin-top: 15px; font-family: Arial, sans-serif; font-size: 14px; text-align: right;'>" +
                "<tr><td style='padding: 6px; color: #777;'>Subtotal</td><td style='padding: 6px; width: 120px; color: " + MAIN_TEXT + ";'>{{VALOR_SUBTOTAL}}</td></tr>" +
                "<tr><td style='padding: 6px; color: #777;'>Descontos</td><td style='padding: 6px; color: " + SECONDARY_TEXT + ";'>- {{VALOR_DESCONTO}}</td></tr>" +
                "<tr><td style='padding: 6px; color: #777;'>Frete</td><td style='padding: 6px; color: " + MAIN_TEXT + ";'>{{VALOR_FRETE}}</td></tr>" +
                "<tr style='font-size: 18px; font-weight: bold; border-top: 1px solid " + SECONDARY_BG + ";'><td style='padding: 15px 6px 0 6px; color: " + MAIN_TEXT + ";'>Total</td><td style='padding: 15px 6px 0 6px; color: " + MAIN_TEXT + ";'>{{VALOR_TOTAL}}</td></tr>" +
                "</table>";

        String detalhes = "{{STEPPER_STATUS}}" +
                "<div style='margin-top: 30px; margin-bottom: 20px;'><h3 style='color: " + MAIN_TEXT + "; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; border-bottom: 2px solid " + MAIN_TEXT + "; padding-bottom: 8px; margin: 0;'>Resumo dos Itens</h3></div>" +
                "{{TABELA_PRODUTOS}}" +
                "<div style='background-color: " + MAIN_BG + "; padding: 20px; border-radius: 4px; margin-top: 20px;'>" +
                resumoValores +
                "</div>" +
                "<div style='margin-top: 40px;'><h3 style='color: " + MAIN_TEXT + "; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; border-bottom: 2px solid " + MAIN_TEXT + "; padding-bottom: 8px; margin: 0; margin-bottom: 15px;'>Endereço de Entrega</h3></div>" +
                "<p style='color: #555; line-height: 1.6; margin: 0;'>{{ENDERECO_ENTREGA}}</p>";

        return titulo + statusMensagem + detalhes;
    }

    /**
     * Wrapper elegante que envelopa todo o conteúdo do e-mail num fundo moderno.
     */
    private String gerarTemplateBase(String tituloHeader, String conteudo) {
        return "<div style=\"background-color: " + MAIN_BG + "; padding: 40px 20px; font-family: 'Arial', Helvetica, sans-serif; -webkit-font-smoothing: antialiased;\">\n" +
                "  <div style=\"max-width: 600px; margin: 0 auto; background-color: " + CARD_BG + "; border-radius: 8px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.05);\">\n" +
                "    <div style=\"background-color: " + MAIN_TEXT + "; padding: 30px; text-align: center;\">\n" +
                "      <h1 style=\"color: #ffffff; margin: 0; font-size: 18px; letter-spacing: 3px; text-transform: uppercase; font-weight: bold;\">Ó T I C A</h1>\n" +
                "    </div>\n" +
                "    <div style=\"padding: 40px 30px;\">\n" +
                "      " + conteudo + "\n" +
                "    </div>\n" +
                "    <div style=\"background-color: " + MAIN_BG + "; padding: 20px; text-align: center; border-top: 1px solid " + SECONDARY_BG + ";\">\n" +
                "      <p style=\"margin: 0; color: #999; font-size: 12px; letter-spacing: 0.5px;\">Obrigado por comprar conosco!</p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>";
    }
    @Async
    public void enviarEmailMarketing(String destinatario, String titulo, String assunto, String conteudoHtml) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setFrom(remetente, "Ótica - Novidades");
            helper.setTo(destinatario);
            helper.setSubject(assunto);

            // Envelopa o conteúdo livre dentro do nosso template moderno
            String htmlMsg = gerarTemplateBase(
                    titulo,
                    "<div style='color: #555; line-height: 1.6; font-size: 15px;'>" +
                            conteudoHtml +
                            "</div>"
            );

            helper.setText(htmlMsg, true);
            mailSender.send(mensagem);

            log.info("E-mail de marketing enviado com sucesso para {}", destinatario);
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de marketing para {}: {}", destinatario, e.getMessage());
        }
    }
}