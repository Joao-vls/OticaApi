package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.UseCases.usuario.ListarEnderecosUseCase;
import br.com.otica.otica_loja.UseCases.pedidos.CancelarPedidoUseCase;
import br.com.otica.otica_loja.dto.BoletoPagamentoRequest;
import br.com.otica.otica_loja.dto.BoletoPagamentoResponse;
import br.com.otica.otica_loja.enums.StatusPedido;
import br.com.otica.otica_loja.enums.TipoEmail;
import br.com.otica.otica_loja.service.email.EmailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Locale;

@Service
public class CriarPagamentoBoletoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ListarEnderecosUseCase listarEnderecosUseCase;

    // 🔥 INJETADO PARA FAZER ROLLBACK
    @Autowired
    private CancelarPedidoUseCase cancelarPedidoUseCase;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Autowired
    private EmailService emailService;

    public BoletoPagamentoResponse criarPagamento(BoletoPagamentoRequest request) {
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido não está aguardando pagamento.");
        }

        Endereco enderecoCliente;
        try {
            enderecoCliente = listarEnderecosUseCase.buscarEnderecoPadrao(request.usuarioId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("O cliente precisa ter um endereço padrão cadastrado para gerar o boleto.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            String totalAmountStr = String.format(Locale.US, "%.2f", pedido.getTotal());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, Object> body = new HashMap<>();
            body.put("type", "online");
            body.put("external_reference", pedido.getId().toString());
            body.put("processing_mode", "automatic");
            body.put("total_amount", totalAmountStr);
            body.put("description", "Pedido Ótica - " + pedido.getId());

            Map<String, Object> payer = new HashMap<>();

            String emailCliente = request.emailCliente();
            if (emailCliente == null || emailCliente.isBlank()) {
                throw new IllegalArgumentException("O e-mail do cliente é obrigatório para emissão do boleto.");
            }
            payer.put("email", emailCliente.trim());

            String nome = request.nomeCliente();
            String sobrenome = request.sobrenomeCliente();

            if (nome == null || nome.isBlank() || sobrenome == null || sobrenome.isBlank()) {
                throw new IllegalArgumentException("Nome e sobrenome do cliente são obrigatórios para emissão do boleto.");
            }

            payer.put("first_name", nome.trim());
            payer.put("last_name", sobrenome.trim());

            Map<String, Object> identification = new HashMap<>();
            identification.put("type", "CPF");

            String cpfLimpo = request.cpfCliente() != null ? request.cpfCliente().replaceAll("[^0-9]", "") : "";
            if (cpfLimpo.length() != 11) {
                throw new IllegalArgumentException("CPF inválido ou incompleto para emissão do boleto.");
            }
            identification.put("number", cpfLimpo);
            payer.put("identification", identification);

            Map<String, Object> address = new HashMap<>();
            String cepLimpo = enderecoCliente.getCep() != null ? enderecoCliente.getCep().replaceAll("[^0-9]", "") : "";
            if (cepLimpo.length() != 8) {
                throw new IllegalArgumentException("O CEP do endereço precisa conter 8 dígitos.");
            }
            address.put("zip_code", cepLimpo);
            address.put("street_name", enderecoCliente.getLogradouro() != null ? enderecoCliente.getLogradouro().trim() : "Rua Principal");

            String numeroReal = enderecoCliente.getNumero();
            String numeroLimpo = (numeroReal != null) ? numeroReal.replaceAll("[^0-9]", "") : "0";
            if (numeroLimpo.isEmpty()) {
                numeroLimpo = "0";
            }
            address.put("street_number", numeroLimpo);
            address.put("neighborhood", enderecoCliente.getBairro() != null ? enderecoCliente.getBairro().trim() : "Centro");
            address.put("city", enderecoCliente.getCidade() != null ? enderecoCliente.getCidade().trim() : "Cidade");

            String uf = enderecoCliente.getEstado() != null ? enderecoCliente.getEstado().trim().toUpperCase() : "MG";
            if (uf.length() > 2) {
                uf = uf.substring(0, 2);
            }
            address.put("federal_unit", uf);

            payer.put("address", address);
            body.put("payer", payer);

            Map<String, Object> paymentMethod = new HashMap<>();
            paymentMethod.put("id", "boleto");
            paymentMethod.put("type", "ticket");

            Map<String, Object> paymentItem = new HashMap<>();
            paymentItem.put("amount", totalAmountStr);
            paymentItem.put("payment_method", paymentMethod);
            paymentItem.put("expiration_time", "P3D");

            Map<String, Object> transactions = new HashMap<>();
            transactions.put("payments", List.of(paymentItem));
            body.put("transactions", transactions);

            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());
            HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> createResponse = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/orders",
                    createEntity,
                    String.class
            );

            JsonNode createRoot = mapper.readTree(createResponse.getBody());

            String orderStatus = createRoot.path("status").asText("");
            if ("failed".equalsIgnoreCase(orderStatus) || "rejected".equalsIgnoreCase(orderStatus)) {
                // 🔥 CANCELA SILENCIOSAMENTE E DEVOLVE O ESTOQUE
                cancelarPedidoUseCase.cancelar(pedido.getId(), pedido.getUsuarioId(), "Pagamento recusado pelo Mercado Pago.", false);
                throw new IllegalArgumentException("Pagamento recusado pelo Mercado Pago. Verifique os dados e tente novamente.");
            }

            JsonNode paymentsArray = createRoot.path("transactions").path("payments");
            String boletoUrl = "";
            String linhaDigitavel = "";

            if (paymentsArray.isArray() && !paymentsArray.isEmpty()) {
                JsonNode firstPayment = paymentsArray.get(0);
                String paymentStatus = firstPayment.path("status").asText("");
                String statusDetail = firstPayment.path("status_detail").asText("");

                if ("rejected".equalsIgnoreCase(paymentStatus) || "failed".equalsIgnoreCase(paymentStatus) || "processing_error".equalsIgnoreCase(statusDetail)) {
                    // 🔥 CANCELA SILENCIOSAMENTE E DEVOLVE O ESTOQUE
                    cancelarPedidoUseCase.cancelar(pedido.getId(), pedido.getUsuarioId(), "CPF recusado pelo banco emissor.", false);
                    throw new IllegalArgumentException("Boleto recusado pelo banco emissor. Verifique se o CPF do titular está correto e válido na Receita Federal.");
                }

                JsonNode methodResponse = firstPayment.path("payment_method");
                boletoUrl = methodResponse.path("ticket_url").asText("");
                linhaDigitavel = methodResponse.path("digitable_line").asText("");
                if (linhaDigitavel.isEmpty()) linhaDigitavel = methodResponse.path("barcode_content").asText("");
            }

            pedido.setStatus(StatusPedido.PROCESSANDO);
            pedido.setAtualizadoEm(OffsetDateTime.now());
            pedidoRepository.save(pedido);

            String blocoBoletoHtml = "<div style='background-color: #f8f9fa; border: 1px dashed #004d99; padding: 20px; text-align: center; margin: 20px 0; border-radius: 8px; font-family: Arial, sans-serif;'>" +
                    "<h3 style='margin-top: 0; color: #004d99;'>Seu boleto foi gerado com sucesso!</h3>" +
                    "<p style='font-size: 14px; color: #333;'>Vencimento em 3 dias úteis.</p>" +
                    "<a href='" + boletoUrl + "' target='_blank' style='display: inline-block; padding: 12px 24px; color: #fff; background-color: #004d99; text-decoration: none; border-radius: 4px; font-weight: bold; margin-bottom: 20px;'>Visualizar Boleto</a><br>" +
                    "<p style='font-size: 13px; color: #555; margin-bottom: 8px;'><b>Ou copie a Linha Digitável:</b></p>" +
                    "<input type='text' value='" + linhaDigitavel + "' readonly style='width: 95%; padding: 12px; border: 1px solid #ced4da; font-size: 12px; text-align: center; background: #ffffff; border-radius: 4px; color: #495057;'>" +
                    "</div>";

            emailService.enviarEmailPedido(pedido, TipoEmail.PEDIDO_REALIZADO, blocoBoletoHtml);

            return new BoletoPagamentoResponse(
                    pedido.getId(),
                    pedido.getTotal(),
                    boletoUrl,
                    linhaDigitavel,
                    "Boleto gerado com sucesso"
            );

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();

            // 🔥 CANCELA SILENCIOSAMENTE E DEVOLVE O ESTOQUE EM QUALQUER ERRO HTTP
            cancelarPedidoUseCase.cancelar(pedido.getId(), pedido.getUsuarioId(), "Erro HTTP Mercado Pago: " + e.getStatusCode(), false);

            if (e.getStatusCode().is4xxClientError()) {
                throw new IllegalArgumentException("Pagamento recusado: O CPF do titular ou os dados informados são inválidos. Verifique e tente novamente.");
            }
            throw new IllegalArgumentException("Não foi possível gerar o boleto no momento. Tente novamente mais tarde.");

        } catch (IllegalArgumentException e) {
            // A exceção já fez o cancelamento acima antes de ser jogada
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            // 🔥 CANCELA SILENCIOSAMENTE E DEVOLVE O ESTOQUE EM CASO DE ERRO INTERNO DO SERVIDOR
            cancelarPedidoUseCase.cancelar(pedido.getId(), pedido.getUsuarioId(), "Erro interno na comunicação com o Mercado Pago.", false);
            throw new IllegalArgumentException("Ocorreu um erro interno ao processar seu pagamento. Tente novamente.");
        }
    }
}