package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.UseCases.usuario.ListarEnderecosUseCase;
import br.com.otica.otica_loja.dto.BoletoPagamentoRequest;
import br.com.otica.otica_loja.dto.BoletoPagamentoResponse;
import br.com.otica.otica_loja.enums.StatusPedido;

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

    @Value("${mercadopago.access-token}")
    private String accessToken;

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

            // ==========================================
            // MONTAGEM DO PAYLOAD DE ORDEM PARA BOLETO
            // ==========================================
            Map<String, Object> body = new HashMap<>();
            body.put("type", "online");
            body.put("external_reference", pedido.getId().toString());
            body.put("processing_mode", "automatic");
            body.put("total_amount", totalAmountStr);
            body.put("description", "Pedido Ótica - " + pedido.getId());

            Map<String, Object> payer = new HashMap<>();
            payer.put("email", request.emailCliente() != null && !request.emailCliente().isBlank() ? request.emailCliente() : "test_user@test.com");

            // Tratamento de Nome e Sobrenome obrigatórios para boletos
            String nome = request.nomeCliente() != null && !request.nomeCliente().isBlank() ? request.nomeCliente().trim() : "Cliente";
            String sobrenome = request.sobrenomeCliente() != null && !request.sobrenomeCliente().isBlank() ? request.sobrenomeCliente().trim() : "Teste";

            payer.put("first_name", nome);
            payer.put("last_name", sobrenome);

            Map<String, Object> identification = new HashMap<>();
            identification.put("type", "CPF");
            String cpfLimpo = request.cpfCliente() != null ? request.cpfCliente().replaceAll("[^0-9]", "") : "00000000000";
            identification.put("number", cpfLimpo);
            payer.put("identification", identification);

            Map<String, Object> address = new HashMap<>();
            address.put("zip_code", enderecoCliente.getCep() != null ? enderecoCliente.getCep().replaceAll("[^0-9]", "") : "39480000");
            address.put("street_name", enderecoCliente.getLogradouro() != null ? enderecoCliente.getLogradouro() : "Rua Principal");
            address.put("street_number", enderecoCliente.getNumero() != null && !enderecoCliente.getNumero().isBlank() ? enderecoCliente.getNumero() : "S/N");
            address.put("neighborhood", enderecoCliente.getBairro() != null ? enderecoCliente.getBairro() : "Centro");
            address.put("city", enderecoCliente.getCidade() != null ? enderecoCliente.getCidade() : "Januaria");

            String uf = enderecoCliente.getEstado() != null ? enderecoCliente.getEstado().trim().toUpperCase() : "MG";
            if (uf.length() > 2) {
                uf = uf.substring(0, 2);
            }
            address.put("state", uf);

            payer.put("address", address);
            body.put("payer", payer);

            // Configuração do Meio de Pagamento (Boleto)
            Map<String, Object> paymentMethod = new HashMap<>();
            paymentMethod.put("id", "boleto");
            paymentMethod.put("type", "ticket");

            Map<String, Object> paymentItem = new HashMap<>();
            paymentItem.put("amount", totalAmountStr);
            paymentItem.put("payment_method", paymentMethod);
            paymentItem.put("expiration_time", "P3D"); // Prazo de vencimento de 3 dias úteis

            Map<String, Object> transactions = new HashMap<>();
            transactions.put("payments", List.of(paymentItem));
            body.put("transactions", transactions);

            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());
            HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(body, headers);

            System.out.println("Enviando requisição de Boleto para Orders API...");

            ResponseEntity<String> createResponse = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/orders",
                    createEntity,
                    String.class
            );

            JsonNode createRoot = mapper.readTree(createResponse.getBody());
            JsonNode paymentsArray = createRoot.path("transactions").path("payments");

            String boletoUrl = "";
            String linhaDigitavel = "";

            if (paymentsArray.isArray() && !paymentsArray.isEmpty()) {
                JsonNode firstPayment = paymentsArray.get(0);
                JsonNode methodResponse = firstPayment.path("payment_method");

                boletoUrl = methodResponse.path("ticket_url").asText("");
                linhaDigitavel = methodResponse.path("digitable_line").asText("");

                if (linhaDigitavel.isEmpty()) {
                    linhaDigitavel = methodResponse.path("barcode_content").asText("");
                }
            }

            pedido.setStatus(StatusPedido.PROCESSANDO);
            pedido.setAtualizadoEm(OffsetDateTime.now());
            pedidoRepository.save(pedido);

            return new BoletoPagamentoResponse(
                    pedido.getId(),
                    pedido.getTotal(),
                    boletoUrl,
                    linhaDigitavel,
                    "Boleto gerado com sucesso"
            );

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            System.err.println("=== ERRO 422 DETALHADO DO MERCADO PAGO (BOLETO) ===");
            System.err.println("Status HTTP: " + e.getStatusCode());
            System.err.println("ResponseBody completo: " + responseBody);
            System.err.println("==================================================");

            throw new RuntimeException("Erro do Mercado Pago no Boleto: " + responseBody, e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar Boleto no Mercado Pago (Orders API): " + e.getMessage(), e);
        }
    }
}